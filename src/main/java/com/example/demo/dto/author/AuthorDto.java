package com.example.demo.dto.author;

import com.example.demo.dto.BookSummaryDto;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String description;
    private String nationality;
    private Set<BookSummaryDto> bookSummaryDtos;
}
