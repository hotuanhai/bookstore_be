package com.example.demo.controller;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.bookEdition.BookEditionSalesDto;
import com.example.demo.dto.genre.GenreSalesDto;
import com.example.demo.entity.user.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.request.OrderRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
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
        OrderDto order = orderService.createOrder(userId, request);
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
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getUserOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir)
    {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<OrderDto> orders = orderService.getUserOrders(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(orders, "User orders retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("=== GET /api/orders endpoint hit ===");
        log.info("Status: {}, Search: {}, Page: {}, Size: {}", status, search, page, size);

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("ASC")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());

        Page<OrderDto> orders = orderService.getOrders(status, search, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(orders, "Orders retrieved successfully"));
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

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        OrderDto order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(
                ApiResponse.success(order, "Order status updated successfully")
        );
    }

    @GetMapping("/top-selling-genres")
    public ResponseEntity<ApiResponse<List<GenreSalesDto>>> getTopSellingGenres(
            @RequestParam(defaultValue = "1m") String period){
        List<GenreSalesDto> topGenres = orderService.getTopSellingGenres(period);
        return ResponseEntity.ok(ApiResponse.success(topGenres, "Top selling genres retrieved successfully"));
    }

    @GetMapping("/top-selling-editions")
    public ResponseEntity<ApiResponse<List<BookEditionSalesDto>>> getTopSellingEditions(
            @RequestParam(defaultValue = "1m") String period,
            @RequestParam(defaultValue = "10") int limit){
        List<BookEditionSalesDto> topEditions = orderService.getTopSellingEditions(period, limit);
        return ResponseEntity.ok(ApiResponse.success(topEditions, "Top selling editions retrieved successfully"));
    }
}