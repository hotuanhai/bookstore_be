package com.example.demo.mapper;

import com.example.demo.dto.author.AuthorDto;
import com.example.demo.dto.author.AuthorSummaryDto;
import com.example.demo.entity.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorMapper {
    private final BookMapper bookMapper;

    public AuthorDto toDto(Author author){
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .nationality(author.getNationality())
                .description(author.getDescription())
                .imageUrl(author.getImageUrl())
                .bookSummaryDtos(author.getBooks().stream()
                        .map(bookMapper::toSummaryDto).collect(Collectors.toSet()))
                .build();
    }

    public AuthorSummaryDto toSummaryDto(Author author){
        return AuthorSummaryDto.builder()
                .id(author.getId())
                .imageUrl(author.getImageUrl())
                .name(author.getName())
                .build();
    }
}
