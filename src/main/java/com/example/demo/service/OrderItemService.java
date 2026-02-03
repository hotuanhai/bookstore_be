package com.example.demo.service;

import com.example.demo.dao.OrderItemRepository;
import com.example.demo.dto.stock.SalesRevenueTrend;
import com.example.demo.dto.stock.TrendDataPoint;
import com.example.demo.enums.OrderStatus;
import com.example.demo.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderStatus> statuses = List.of(
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED
        );
        return orderItemRepository.getTotalRevenueByDateRange(startDate, endDate, statuses);
    }

    @Transactional(readOnly = true)
    public SalesRevenueTrend getSalesRevenueTrend(String period) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        List<TrendDataPoint> dataPoints;
        String groupingType;

        // Define statuses to include (exclude CANCELLED, REFUNDED, etc.)
        List<OrderStatus> validStatuses = List.of(
                OrderStatus.PROCESSING,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED
        );

        if (period == null || period.trim().isEmpty() || "ALL".equalsIgnoreCase(period)) {
            // Get all-time data
            startDate = LocalDateTime.of(2025, 11, 1, 0, 0);
            List<Object[]> rawData = orderItemRepository.getMonthlyTrendDataRaw(validStatuses, startDate, endDate);
            dataPoints = mapToTrendDataPoints(rawData);
            groupingType = "MONTHLY";
            period = "ALL";
        } else {
            startDate = TimeUtils.calculateStartDate(period);
            long daysBetween = java.time.Duration.between(startDate, endDate).toDays();

            if (daysBetween <= 30) {
                List<Object[]> rawData = orderItemRepository.getDailyTrendDataRaw(validStatuses, startDate, endDate);
                dataPoints = mapToTrendDataPoints(rawData);
                groupingType = "DAILY";
            } else if (daysBetween <= 180) {
                List<Object[]> rawData = orderItemRepository.getWeeklyTrendDataRaw(validStatuses, startDate, endDate);
                dataPoints = mapToTrendDataPoints(rawData);
                groupingType = "WEEKLY";
            } else {
                List<Object[]> rawData = orderItemRepository.getMonthlyTrendDataRaw(validStatuses, startDate, endDate);
                dataPoints = mapToTrendDataPoints(rawData);
                groupingType = "MONTHLY";
            }
        }

        // Fill empty datapoints
        dataPoints = fillEmptyDataPoints(dataPoints, startDate, endDate, groupingType);

        // Calculate totals
        Long totalStockOut = dataPoints.stream()
                .mapToLong(TrendDataPoint::getTotalStockOut)
                .sum();

        BigDecimal totalRevenue = dataPoints.stream()
                .map(TrendDataPoint::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SalesRevenueTrend(period, totalStockOut, totalRevenue, dataPoints);
    }

    //utils
    private List<TrendDataPoint> fillEmptyDataPoints(
            List<TrendDataPoint> existingData,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String groupingType) {

        Map<String, TrendDataPoint> dataMap = existingData.stream()
                .collect(Collectors.toMap(
                        TrendDataPoint::getDate,
                        point -> point,
                        (existing, replacement) -> existing
                ));

        List<TrendDataPoint> filledData = new ArrayList<>();
        LocalDateTime current = startDate;
        DateTimeFormatter formatter;

        switch (groupingType) {
            case "DAILY":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                while (!current.isAfter(endDate)) {
                    String dateKey = current.format(formatter);
                    filledData.add(dataMap.getOrDefault(
                            dateKey,
                            new TrendDataPoint(dateKey, 0L, BigDecimal.ZERO)
                    ));
                    current = current.plusDays(1);
                }
                break;

            case "WEEKLY":
                while (!current.isAfter(endDate)) {
                    int year = current.getYear();
                    int week = current.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
                    String dateKey = String.format("%d-W%02d", year, week);

                    filledData.add(dataMap.getOrDefault(
                            dateKey,
                            new TrendDataPoint(dateKey, 0L, BigDecimal.ZERO)
                    ));
                    current = current.plusWeeks(1);
                }
                break;

            case "MONTHLY":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                while (!current.isAfter(endDate)) {
                    String dateKey = current.format(formatter);
                    filledData.add(dataMap.getOrDefault(
                            dateKey,
                            new TrendDataPoint(dateKey, 0L, BigDecimal.ZERO)
                    ));
                    current = current.plusMonths(1);
                }
                break;
        }

        return filledData;
    }

    private List<TrendDataPoint> mapToTrendDataPoints(List<Object[]> rawData) {
        return rawData.stream()
                .map(row -> new TrendDataPoint(
                        (String) row[0],                    // date
                        ((Number) row[1]).longValue(),      // totalStockOut
                        (BigDecimal) row[2]                 // totalRevenue
                ))
                .collect(Collectors.toList());
    }
}
