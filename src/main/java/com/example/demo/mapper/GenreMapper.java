package com.example.demo.mapper;

import com.example.demo.dto.genre.GenreDto;
import com.example.demo.entity.genre.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {
    public GenreDto toDto(Genre genre) {
        return GenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
