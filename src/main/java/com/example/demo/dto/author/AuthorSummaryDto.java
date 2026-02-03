package com.example.demo.dto.author;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorSummaryDto {
    private Long id;
    private String name;
    private String imageUrl;
}
