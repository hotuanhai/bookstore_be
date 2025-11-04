package com.example.demo.dto;

import com.example.demo.enums.BookStatus;
import lombok.*;
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
    private Set<BookEditionDto> bookEditionDtos;
    private BookStatus status;

    private Long seriesId;
    private String seriesName;
}
