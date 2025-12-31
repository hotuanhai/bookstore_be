package com.example.demo.request;

import com.example.demo.enums.OrderMethod;
import com.example.demo.enums.PaymentMethod;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private String address;
    private String phoneNumber;
    private String name;
    @Builder.Default
    private String description = "";
    private PaymentMethod paymentMethod;
    private OrderMethod orderMethod;

    // for the direct purchase only
    private Long editionId;
    private Integer quantity;
}
