package com.example.demo.dto.bookEdition;

import com.example.demo.enums.BookStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEditionSalesDto {
    private Long id;
    private Long editionId;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private BookStatus status;
    //for discount
    private double discountPercentage = 0;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;

    //for admin
    private Integer totalSold;
}
