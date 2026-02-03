package com.example.demo.dto.stock;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRevenueTrend {
    private String period;                    // "7d", "1m", ..
    private Long totalStockOut;
    private BigDecimal totalRevenue;
    private List<TrendDataPoint> dataPoints;
}
