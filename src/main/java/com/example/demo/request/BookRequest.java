package com.example.demo.request;

import com.example.demo.enums.BookStatus;
import com.example.demo.enums.EditionFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {
    @NotNull(message = "Book title is required")
    @NotBlank(message = "Book title cannot be blank")
    private String title;

    @NotEmpty(message = "At least one author is required")
    private Set<Long> authorIds;

    @NotEmpty(message = "At least one genre is required")
    private Set<String> genres;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    //edition
    @NotNull(message = "Edition name is required")
    @NotBlank(message = "Edition name cannot be blank")
    private String name;

    @NotEmpty(message = "Book image is required")
    private String imageUrl;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Integer price = 0;

    private String description;

    @NotNull(message = "Book isbn is required")
    @NotBlank(message = "Book isbn cannot be blank")
    private String isbn;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;

    @Min(value = 1000, message = "Published year must be valid")
    private Integer publishedYear;

    private String publisher;

    @Enumerated(EnumType.STRING)
    private BookStatus editionStatus;

    private String language;

    private EditionFormat format;

    @Builder.Default
    private Integer discountPercentage = 0;

    private LocalDateTime discountStartDate;

    private LocalDateTime discountEndDate;

    @AssertTrue(message = "Discount end date must be after start date")
    public boolean isValidDiscountDates() {
        if (discountStartDate != null && discountEndDate != null) {
            return discountEndDate.isAfter(discountStartDate);
        }
        return true;
    }
}
