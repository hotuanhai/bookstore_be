package com.example.demo.mapper;

import com.example.demo.dto.SeriesDto;
import com.example.demo.entity.book.Series;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeriesMapper {
    private final BookMapper bookMapper;

    public SeriesDto toDto(Series series){
        return SeriesDto.builder()
                .id(series.getId())
                .name(series.getName())
                .description(series.getDescription())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .bookSummaryDtos(series.getBooks().stream()
                        .map(bookMapper::toSummaryDto).collect(Collectors.toSet()))
                .build();
    }
}
