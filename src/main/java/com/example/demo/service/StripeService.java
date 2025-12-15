package com.example.demo.service;

import com.example.demo.config.StripeConfiguration;
import com.example.demo.dao.PaymentRepository;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.payment.StripePaymentRequestDTO;
import com.example.demo.dto.payment.StripePaymentResponseDTO;
import com.example.demo.entity.Payment;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.request.CheckoutRequest;
import com.example.demo.request.OrderRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    private final StripeConfiguration stripeConfig;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * func to handle both createCheckoutSession and order
     */
    @Transactional
    public StripePaymentResponseDTO createCheckout(Long userId, OrderRequest request){
        try {
            log.info("Starting createCheckout for userId: {}", userId);

            OrderDto orderDto = orderService.createOrderFromCart(userId, request);
            log.info("Order created successfully: {}", orderDto.getId());

            StripePaymentRequestDTO paymentRequest
                    = StripePaymentRequestDTO.builder()
                    .orderId(orderDto.getId().toString())
                    .amount(orderDto.getTotalAmount())
                    .currency("usd")
                    .customerEmail(orderDto.getUserName())
                    .customerName(request.getName())
                    .description(request.getDescription())
                    .build();

            log.info("Creating Stripe checkout session for order: {}",
                    paymentRequest.getOrderId());

            return createCheckoutSession(paymentRequest);

        } catch (Exception e) {
            log.error("Error in createCheckout: ", e);
            throw e; // Re-throw to see it in controller
        }
    }

    /**
     * Create Stripe Checkout Session (Recommended method)
     */
    @Transactional
    public StripePaymentResponseDTO createCheckoutSession(StripePaymentRequestDTO request) {
        try {
            // 1. Save payment to database
            Payment payment = createPaymentRecord(request);

            // 2. Build Checkout Session parameters
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(stripeConfig.getSuccessUrl())
                    .setCancelUrl(stripeConfig.getCancelUrl())
                    // Cấu hình này giúp Metadata xuất hiện trong sự kiện 'payment_intent.succeeded'
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("transactionId", payment.getTransactionId())
                                    .putMetadata("orderId", request.getOrderId()) // JobId
                                    .build()
                    )
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(request.getCurrency())
                                                    .setUnitAmount(convertToStripeAmount(request.getAmount()))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(request.getDescription() != null ?
                                                                            request.getDescription() : "Payment")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    // Metadata này chỉ dành cho 'checkout.session.completed'
                    .putMetadata("orderId", request.getOrderId())
                    .putMetadata("transactionId", payment.getTransactionId());

            // Add customer email if provided
            if (request.getCustomerEmail() != null) {
                paramsBuilder.setCustomerEmail(request.getCustomerEmail());
                paramsBuilder.putMetadata("customerEmail", request.getCustomerEmail());
            }

            // Create Stripe Checkout Session
            Session session = Session.create(paramsBuilder.build());

            // 3. Update payment with Stripe session ID
            payment.setProviderTransactionId(session.getId());
            paymentRepository.save(payment);

            log.info("Created Stripe checkout session: {} for order: {}",
                    session.getId(), request.getOrderId());

            return StripePaymentResponseDTO.builder()
                    .checkoutSessionId(session.getId())
                    .checkoutUrl(session.getUrl())
                    .status(PaymentStatus.PENDING)
                    .message("Checkout session created successfully")
//                    .amount(request.getAmount())
//                    .currency(request.getCurrency())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error creating checkout session: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage(), e);
        }
    }

    /**
     * Create Payment Intent (for custom payment flows)
     */
    @Transactional
    public StripePaymentResponseDTO createPaymentIntent(StripePaymentRequestDTO request) {
        try {
            // 1. Save to database
            Payment payment = createPaymentRecord(request);

            // 2. Create Payment Intent
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(convertToStripeAmount(request.getAmount()))
                    .setCurrency(request.getCurrency())
                    .setDescription(request.getDescription())
                    .putMetadata("orderId", request.getOrderId())
                    .putMetadata("transactionId", payment.getTransactionId())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );

            // Add customer if provided
            if (request.getCustomerId() != null) {
                paramsBuilder.setCustomer(request.getCustomerId());
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            // 3. Update database
            payment.setProviderTransactionId(paymentIntent.getId());
            paymentRepository.save(payment);

            log.info("Created Stripe payment intent: {} for order: {}",
                    paymentIntent.getId(), request.getOrderId());

            return StripePaymentResponseDTO.builder()
                    .paymentIntentId(paymentIntent.getId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(mapStripeStatus(paymentIntent.getStatus()))
                    .message("Payment intent created successfully")
//                    .amount(request.getAmount())
//                    .currency(request.getCurrency())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }


    /**
     * Handle Stripe Webhook Events
     */
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(
                    payload, sigHeader, stripeConfig.getWebhookSecret()
            );

            log.info("Received Stripe webhook event: {}", event.getType());

            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutCompleted(event);
                    break;
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                default:
                    log.info("Unhandled webhook event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject().orElseThrow(() -> new RuntimeException("Failed to deserialize session"));

        String transactionId = session.getMetadata().get("transactionId");
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setResponseCode("checkout.session.completed");
        payment.setResponseMessage("Payment completed via Stripe Checkout");

        paymentRepository.save(payment);

        log.info("Checkout completed for transaction: {}", transactionId);

        // Update order status
        String orderId = session.getMetadata().get("orderId");
        if (orderId != null) {
            try {
                orderService.updatePaymentStatus(Long.parseLong(orderId), PaymentStatus.PAID);
                orderService.updateOrderStatus(Long.parseLong(orderId), OrderStatus.PROCESSING);

                log.info("Checkout completed and order updated: orderId={}, transactionId={}",
                        orderId, transactionId);
            } catch (Exception e) {
                log.error("Failed to update order status for orderId: {}", orderId, e);
            }
        }
    }

    private void handlePaymentSucceeded(Event event) {
        try {
            // Properly deserialize the PaymentIntent
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                log.error("Failed to deserialize PaymentIntent from event: {}", event.getId());
                throw new RuntimeException("Failed to deserialize payment intent");
            }

            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;

            log.info("Processing payment_intent.succeeded for: {}", paymentIntent.getId());

            // Get metadata - THIS IS CRITICAL
            Map<String, String> metadata = paymentIntent.getMetadata();

            if (metadata == null || !metadata.containsKey("orderId")) {
                log.error("PaymentIntent {} missing orderId in metadata", paymentIntent.getId());
                // Don't throw - payment succeeded but we can't find the order
                return;
            }

            // Update order
            String orderId = paymentIntent.getMetadata().get("orderId");
            if (orderId != null) {
                try {
                    // Update order status
                    orderService.updatePaymentStatus(Long.parseLong(orderId), PaymentStatus.PAID);
                    orderService.updateOrderStatus(Long.parseLong(orderId), OrderStatus.PROCESSING);
                    log.info("Payment succeeded and order updated: orderId={}, paymentIntentId={}",
                            orderId, paymentIntent.getId());
                } catch (Exception e) {
                    log.error("Failed to update order status for orderId: {}", orderId, e);
                }
            } else {
                log.warn("No orderId found in payment intent metadata: {}", paymentIntent.getId());
            }
        } catch (ClassCastException e) {
            log.error("Error casting to PaymentIntent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deserialize payment intent", e);
        }
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElseThrow(() -> new RuntimeException("Failed to deserialize payment intent"));

        String transactionId = paymentIntent.getMetadata().get("transactionId");
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setResponseMessage(paymentIntent.getLastPaymentError() != null ?
                paymentIntent.getLastPaymentError().getMessage() : "Payment failed");

        paymentRepository.save(payment);
        log.warn("Payment failed: {}", paymentIntent.getId());
    }

    /**
     * Query Payment Status
     */
    public String queryPaymentStatus(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));

        return payment.getStatus().name();
    }

    /**
     * Get Payment Details
     */
    public Payment getPaymentDetails(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));
    }

    // ===== Helper Methods =====

    private Payment createPaymentRecord(StripePaymentRequestDTO request) {
        Payment payment = Payment.builder()
                .transactionId(generateTransactionId())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(PaymentMethod.STRIPE)
                .status(PaymentStatus.PENDING)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        // Save metadata
        if (request.getCustomerEmail() != null) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("customerEmail", request.getCustomerEmail());
            metadata.put("customerName", request.getCustomerName());
            try {
                payment.setMetadata(new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(metadata));
            } catch (Exception e) {
                log.warn("Failed to serialize metadata", e);
            }
        }

        return paymentRepository.save(payment);
    }

    private void updatePaymentFromIntent(PaymentIntent paymentIntent) {
        String transactionId = paymentIntent.getMetadata().get("transactionId");
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(mapStripeStatusToEnum(paymentIntent.getStatus()));

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }

        payment.setResponseMessage(paymentIntent.getStatus());
        paymentRepository.save(payment);
    }

    private String generateTransactionId() {
        return "STRIPE_" + System.currentTimeMillis() + "_" +
                (int) (Math.random() * 10000);
    }

    private Long convertToStripeAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(100)).longValue();
    }

    private PaymentStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.PAID;
            case "processing",
                 "requires_payment_method",
                 "requires_confirmation",
                 "requires_action"
                    -> PaymentStatus.PENDING;
            case "canceled" -> PaymentStatus.CANCEL;
            default -> PaymentStatus.FAILED;
        };
    }

    private PaymentStatus mapStripeStatusToEnum(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.PAID;
            case "processing", "requires_payment_method", "requires_confirmation", "requires_action" ->
                    PaymentStatus.PENDING;
            case "canceled" -> PaymentStatus.CANCEL;
            default -> PaymentStatus.FAILED;
        };
    }
}