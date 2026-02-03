package com.example.demo.dto;

import com.example.demo.dto.genre.GenreDto;
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
