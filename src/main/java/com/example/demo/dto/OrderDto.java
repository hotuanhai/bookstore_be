package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private String userName;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private String address;
    private String phoneNumber;
    private String name;
    private PaymentMethod paymentMethod;
    private String trackingNumber;
    private List<OrderItemDto> items;

    //stripe
    private String paymentIntentId;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String paymentFailureReason;
}
