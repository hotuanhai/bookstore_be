package com.example.demo.mapper;

import com.example.demo.dto.GenreDto;
import com.example.demo.dto.MainGenreDto;
import com.example.demo.entity.genre.Genre;
import com.example.demo.entity.genre.MainGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Create a mapper to avoid duplication
@Component
@RequiredArgsConstructor
public class MainGenreMapper {
    private final GenreMapper genreMapper;

    public MainGenreDto toDto(MainGenre mainGenre) {
        return MainGenreDto.builder()
                .id(mainGenre.getId())
                .name(mainGenre.getName())
                .genres(mainGenre.getGenres() != null
                        ? mainGenre.getGenres().stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toSet())
                        : Set.of())
                .build();
    }
}
