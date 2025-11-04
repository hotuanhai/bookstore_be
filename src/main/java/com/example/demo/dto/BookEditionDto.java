package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import com.example.demo.enums.EditionFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEditionDto {
    private Long id;
    //info
    private String name;
    private String isbn;
    private String dimension;
    private Integer numberOfPages;
    private Integer publishedYear;
    private String publisher;
    private String description;
    private String language;
    private String translator;
    private EditionFormat format;
    //price
    private int price;
    private int stock;
    private String imageUrl;
    private int discountPercentage;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private BookStatus status;
}
