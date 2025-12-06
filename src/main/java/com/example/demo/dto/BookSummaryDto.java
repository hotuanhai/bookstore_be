package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDto {
    private Long id;
    private Long editionId;
    private String title;
    private String imageUrl;
    private int price;
    private BookStatus status;
    //for discount
    private double discountPercentage = 0;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
}
