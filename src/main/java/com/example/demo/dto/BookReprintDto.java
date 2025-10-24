package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReprintDto {
    private Long id;
    private Long editionId;
    private int reprintNo;
    private int price;
    private int stock;
    private String imageUrl;
    private String reprintNotes;
    @Builder.Default
    private int discountPercentage = 0;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
//    private LocalDateTime printDate;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
}
