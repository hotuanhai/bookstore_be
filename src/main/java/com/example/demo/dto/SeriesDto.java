package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDto {
    private Long id;
    private String name;
    private String description;
    private Set<BookSummaryDto> bookSummaryDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
