package com.example.demo.dto;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEditionDto {
    private Long id;
    private Long bookId;
    private Integer editionNo;
    private String title;
    private String isbn;
    private String dimension;
    private Integer numberOfPages;
    private Integer publishedYear;
    private String description;
    private String translator;
    private Set<AuthorSummaryDto> authors;
    private List<BookReprintDto> reprints;  // Includes the first reprint
}
