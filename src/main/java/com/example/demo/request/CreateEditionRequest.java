package com.example.demo.request;

import com.example.demo.enums.BookStatus;
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

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEditionRequest {
    //edition

    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "ISBN is required")
    private String isbn;
    private String dimension;
    @Min(value = 1, message = "Number of pages must be at least 1")
    private int numberOfPages;
    @Min(value = 1000, message = "Published year must be valid")
    private int publishedYear;
    private String description;
    private String translator;
    @NotEmpty(message = "At least one author is required")
    private Set<Long> authorIds;

    // for first print info

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

