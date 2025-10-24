package com.example.demo.dto;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainGenreDto {
    private Long id;
    private String name;
    private Set<GenreDto> genres;
}
