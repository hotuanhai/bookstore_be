package com.example.demo.dto.stock;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TrendDataPoint {
    private String date;
    private Long totalStockOut;
    private BigDecimal totalRevenue;

    public TrendDataPoint(String date, Long totalStockOut, BigDecimal totalRevenue) {
        this.date = date;
        this.totalStockOut = totalStockOut;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }
}