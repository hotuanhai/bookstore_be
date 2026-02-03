package com.example.demo.dao;

import com.example.demo.entity.order.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND " +
            "(LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findByStatusAndSearchTerm(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);

    Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    Long countByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    Optional<Order> findByTrackingNumber(String trackingNumber);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Order> searchAllOrders(@Param("search") String search, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(o.id AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findByStatusAndSearch(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable);

    List<Order> findByStatusInAndOrderDateAfter(List<OrderStatus> statuses, LocalDateTime startDate);
}
