package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long editionId;
    private String bookTitle;
    private String editionName;
    private String imageUrl;
    private int quantity;

    private BigDecimal originalPrice;
    private BigDecimal currentPrice;
    private BigDecimal subTotal;

    private BookStatus status;
    private int availableStock;
    private LocalDateTime addedAt;
}
