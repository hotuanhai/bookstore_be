package com.example.demo.entity.order;

import com.example.demo.entity.user.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String address;
    private String phoneNumber;
    private String name;
    private PaymentMethod paymentMethod;
    private String trackingNumber;

//    strype
    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_failure_reason")
    private String paymentFailureReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    //func
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }


    public void calculateTotals() {
        this.subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate shipping cost based on subtotal
        this.shippingCost = calculateShippingCost(this.subtotal);
        this.totalAmount = subtotal.add(shippingCost);
    }

    private BigDecimal calculateShippingCost(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            return BigDecimal.ZERO;
        }
        else {
            return new BigDecimal("20000");
        }
    }
}
