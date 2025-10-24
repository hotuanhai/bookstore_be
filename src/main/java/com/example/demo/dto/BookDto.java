package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private Set<AuthorSummaryDto> authors;
    private Set<String> genres;
    private String imageUrl;
    private int price;
    private int numberOfPages;
    private String description;
    private int editionNo;
    private int reprintNo;
    private String isbn;
    private int stock;
    private int publishedYear;
    private String dimension;
    private BookStatus status;
    private String translator;
    private String reprintNotes;
    //for discount
    private int discountPercentage = 0;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
}
