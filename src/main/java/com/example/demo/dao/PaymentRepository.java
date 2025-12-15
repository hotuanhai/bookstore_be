package com.example.demo.dao;

import com.example.demo.entity.Payment;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Payment> findByPaymentMethodAndStatus(PaymentMethod method, PaymentStatus status);
}