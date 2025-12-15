package com.example.demo.request;

import com.example.demo.enums.BookStatus;
import com.example.demo.enums.EditionFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditionRequest {
    //edition
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "ISBN is required")
    private String isbn;
    private int publishedYear;
    private String description;
    private String language;
    private EditionFormat format;
    //price
    @Min(value = 0, message = "Price must be non-negative")
    private BigDecimal price;
    @Min(value = 0, message = "Stock must be non-negative")
    private int stock;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    @Min(0) @Max(100)
    private int discountPercentage;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
}

