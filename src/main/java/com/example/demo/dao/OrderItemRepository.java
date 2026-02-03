package com.example.demo.dao;

import com.example.demo.dto.stock.TrendDataPoint;
import com.example.demo.entity.order.OrderItem;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByEditionId(Long editionId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.edition.id = :editionId")
    Long getTotalQuantitySold(@Param("editionId") Long editionId);

    @Query("""
                SELECT COALESCE(SUM(oi.quantity * oi.priceAtPurchase), 0)
                FROM OrderItem oi
                WHERE oi.order.status IN :statuses
                  AND oi.order.orderDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal getTotalRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<OrderStatus> statuses
    );

    // Get daily sales and revenue data from orders
    @Query("""
    SELECT
        FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m-%d'),
        SUM(oi.quantity),
        SUM(oi.quantity * oi.priceAtPurchase)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.status IN :statuses
    AND o.orderDate BETWEEN :startDate AND :endDate
    GROUP BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m-%d')
    ORDER BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m-%d')
    """)
    List<Object[]> getDailyTrendDataRaw(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get weekly sales and revenue data from orders
    @Query("""
    SELECT
        CONCAT(FUNCTION('YEAR', o.orderDate), '-W', FUNCTION('WEEK', o.orderDate)),
        SUM(oi.quantity),
        SUM(oi.quantity * oi.priceAtPurchase)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.status IN :statuses
    AND o.orderDate BETWEEN :startDate AND :endDate
    GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('WEEK', o.orderDate)
    ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('WEEK', o.orderDate)
    """)
    List<Object[]> getWeeklyTrendDataRaw(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get monthly sales and revenue data from orders
    @Query("""
    SELECT
        FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m'),
        SUM(oi.quantity),
        SUM(oi.quantity * oi.priceAtPurchase)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.status IN :statuses
    AND o.orderDate BETWEEN :startDate AND :endDate
    GROUP BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m')
    ORDER BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m')
    """)
    List<Object[]> getMonthlyTrendDataRaw(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
