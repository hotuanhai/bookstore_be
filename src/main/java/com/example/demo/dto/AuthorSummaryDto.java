package com.example.demo.dto;

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
