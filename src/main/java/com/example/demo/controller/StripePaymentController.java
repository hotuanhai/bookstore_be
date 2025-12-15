package com.example.demo.controller;

import com.example.demo.dto.payment.StripePaymentRequestDTO;
import com.example.demo.dto.payment.StripePaymentResponseDTO;
import com.example.demo.entity.user.User;
import com.example.demo.request.CheckoutRequest;
import com.example.demo.request.OrderRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final StripeService stripeService;

    @PostMapping("/order-checkout")
    public ResponseEntity<ApiResponse<StripePaymentResponseDTO>> createCheckout(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal User user
    ) {
        log.info("Creating checkout for user: {}", user.getId());
        StripePaymentResponseDTO response = stripeService.createCheckout(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Checkout session created successfully"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<StripePaymentResponseDTO>> createCheckoutSession(
            @Valid @RequestBody StripePaymentRequestDTO request) {
        log.info("Creating Stripe checkout session for order: {}", request.getOrderId());
        StripePaymentResponseDTO response = stripeService.createCheckoutSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Checkout session created successfully"));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook");

        try {
            stripeService.handleWebhook(payload, sigHeader);
            return ResponseEntity.ok(ApiResponse.success("Webhook processed", "Webhook processed successfully"));
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Webhook processing failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<String>> queryPayment(@PathVariable String transactionId) {
        log.info("Querying Stripe payment: {}", transactionId);
        String status = stripeService.queryPaymentStatus(transactionId);
        return ResponseEntity.ok(ApiResponse.success(status, "Payment status retrieved successfully"));
    }
}