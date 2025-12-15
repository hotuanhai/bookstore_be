package com.example.demo.dao;

import com.example.demo.entity.order.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    Long countByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    Optional<Order> findByTrackingNumber(String trackingNumber);
}
