package com.example.demo.dto.payment;

import com.example.demo.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentResponseDTO {
    private String paymentIntentId;
    private String clientSecret;
    private String checkoutSessionId;
    private String checkoutUrl;
    private PaymentStatus status;
    private String message;
}