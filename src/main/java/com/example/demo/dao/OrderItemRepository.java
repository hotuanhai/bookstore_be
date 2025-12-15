package com.example.demo.dao;

import com.example.demo.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByEditionId(Long editionId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.edition.id = :editionId")
    Long getTotalQuantitySold(@Param("editionId") Long editionId);
}
