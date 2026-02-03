package com.example.demo.controller;

import com.example.demo.dto.stock.SalesRevenueTrend;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenueByDate(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        BigDecimal revenue = orderItemService.getTotalRevenue(start, end);
        return ResponseEntity.ok(ApiResponse.success(revenue, "Revenue fetched successfully"));
    }

    @GetMapping("/trend/sales-revenue")
    public ResponseEntity<ApiResponse<SalesRevenueTrend>> getSalesRevenueTrend(
            @RequestParam(required = false) String period) {

        SalesRevenueTrend trend = orderItemService.getSalesRevenueTrend(period);

        return ResponseEntity.ok(ApiResponse.success(
                trend,
                "Sales and revenue trend retrieved successfully"
        ));
    }
}
