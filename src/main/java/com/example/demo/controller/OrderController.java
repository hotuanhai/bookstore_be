package com.example.demo.controller;

import com.example.demo.dto.OrderDto;
import com.example.demo.enums.OrderStatus;
import com.example.demo.request.OrderRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // Create order from cart
    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @PathVariable Long userId,
            @RequestBody OrderRequest request) {
        OrderDto order = orderService.createOrderFromCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(order, "Order created successfully"));
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable Long orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
    }

    // Get user orders
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(ApiResponse.success(orders, "User orders retrieved successfully"));
    }

    // Get all orders (admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "All orders retrieved successfully"));
    }

    // Update order status (admin)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDto order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
    }

    // Add tracking number (admin)
    @PutMapping("/{orderId}/tracking")
    public ResponseEntity<ApiResponse<OrderDto>> addTracking(
            @PathVariable Long orderId,
            @RequestParam String trackingNumber) {
        OrderDto order = orderService.addTrackingNumber(orderId, trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(order, "Tracking number added successfully"));
    }

    // Cancel order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable Long orderId) {
        OrderDto order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"));
    }

    // Track order by tracking number
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ApiResponse<OrderDto>> trackOrder(@PathVariable String trackingNumber) {
        OrderDto order = orderService.trackOrder(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(order, "Order tracked successfully"));
    }

    // Get orders by status (admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderDto> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }
}