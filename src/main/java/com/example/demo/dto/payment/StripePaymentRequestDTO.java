package com.example.demo.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentRequestDTO {
    @NotNull
    private String orderId;

    @NotNull
    private BigDecimal amount;

    @Builder.Default
    private String currency = "usd";
    private String description;
    private String customerEmail;
    private String customerName;

    // For saved cards
    private String customerId;
}