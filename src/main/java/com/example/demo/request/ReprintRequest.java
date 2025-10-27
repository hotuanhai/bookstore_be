package com.example.demo.request;

import com.example.demo.enums.BookStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReprintRequest {
    @Min(value = 0, message = "Price must be non-negative")
    private int price;
    @Min(value = 0, message = "Stock must be non-negative")
    private int stock;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    private String reprintNotes;
    @Min(0) @Max(100)
    private int discountPercentage;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
}
