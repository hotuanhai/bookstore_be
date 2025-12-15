package com.example.demo.request;

import com.example.demo.dto.payment.StripePaymentRequestDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private OrderRequest orderRequest;
    private StripePaymentRequestDTO stripeRequest;
}
