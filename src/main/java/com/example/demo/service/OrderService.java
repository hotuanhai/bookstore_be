package com.example.demo.service;

import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dao.OrderItemRepository;
import com.example.demo.dao.OrderRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.CartDto;
import com.example.demo.dto.CartItemDto;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderItemDto;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.cart.CartItem;
import com.example.demo.entity.order.Order;
import com.example.demo.entity.order.OrderItem;
import com.example.demo.entity.user.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.exception.EditionNotFoundException;
import com.example.demo.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final BookEditionService bookEditionService;
    private final BookEditionRepository bookEditionRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderDto createOrderFromCart(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartDto cart = cartService.getCart(userId);
        List<CartItemDto> cartItemDtos = cart.getItems();

        if (cartItemDtos.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .address(request.getAddress())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        // Convert cart items to order items
        for (CartItemDto cartItemDto : cartItemDtos) {
            BookEdition edition = bookEditionRepository.findById(cartItemDto.getEditionId())
                    .orElseThrow(() -> new EditionNotFoundException("Edition not found: " + cartItemDto.getEditionId()));

            // Check stock
            if (edition.getStock() < cartItemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + edition.getBook().getTitle());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .edition(edition)
                    .quantity(cartItemDto.getQuantity())
                    .priceAtPurchase(edition.getPrice())
                    .productName(edition.getBook().getTitle() + " - " + edition.getName())
                    .build();

            order.addOrderItem(orderItem);

            // Reduce stock
            edition.setStock(edition.getStock() - cartItemDto.getQuantity());
            bookEditionRepository.save(edition);
        }

        order.calculateTotals();
        order = orderRepository.save(order);

        cartService.clearCart(userId);

        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order = orderRepository.save(order);
        return mapToDto(order);
    }

    @Transactional
    public OrderDto updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setPaymentStatus(paymentStatus);
        order = orderRepository.save(order);

        log.info("Updated payment status for order {}: {}", orderId, paymentStatus);
        return mapToDto(order);


    }

    @Transactional
    public OrderDto addTrackingNumber(Long orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setTrackingNumber(trackingNumber);
        order.setStatus(OrderStatus.SHIPPED);
        order = orderRepository.save(order);
        return mapToDto(order);
    }

    // Cancel order
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped/delivered order");
        }

        // Check if payment was successful
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid order. Please request refund.");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            BookEdition edition = item.getEdition();
            edition.setStock(edition.getStock() + item.getQuantity());
            bookEditionRepository.save(edition);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto trackOrder(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with tracking number: " + trackingNumber));
        return mapToDto(order);
    }

    // Get all orders (admin)
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Helper method to map entity to DTO
    private OrderDto mapToDto(Order order) {
        OrderDto dto = OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getUsername())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .totalAmount(order.getTotalAmount())
                .address(order.getAddress())
                .name(order.getName())
                .phoneNumber(order.getPhoneNumber())
                .paymentMethod(order.getPaymentMethod())
                .trackingNumber(order.getTrackingNumber())
                .paymentIntentId(order.getPaymentIntentId())
                .paymentStatus(order.getPaymentStatus())
                .paymentFailureReason(order.getPaymentFailureReason())
                .paidAt(order.getPaidAt())
                .build();

        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        return dto;
    }

    private OrderItemDto mapItemToDto(OrderItem item) {
        OrderItemDto dto = OrderItemDto.builder()
                .editionId(item.getEdition().getId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .totalPrice(item.getTotalPrice())
                .build();
        return dto;
    }
}
