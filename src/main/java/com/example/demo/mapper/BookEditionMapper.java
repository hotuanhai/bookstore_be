package com.example.demo.mapper;

import com.example.demo.dto.BookEditionDto;
import com.example.demo.entity.book.BookEdition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookEditionMapper {
    private final AuthorMapper authorMapper;
    private final BookReprintMapper bookReprintMapper;

    public BookEditionDto toDto(BookEdition bookEdition){
        return BookEditionDto.builder()
                .id(bookEdition.getId())
                .bookId(bookEdition.getBook().getId())
                .editionNo(bookEdition.getEditionNo())
                .title(bookEdition.getTitle())
                .isbn(bookEdition.getIsbn())
                .dimension(bookEdition.getDimension())
                .numberOfPages(bookEdition.getNumberOfPages())
                .publishedYear(bookEdition.getPublishedYear())
                .description(bookEdition.getDescription())
                .translator(bookEdition.getTranslator())
                .authors(bookEdition.getAuthors().stream().map(authorMapper::toSummaryDto)
                        .collect(Collectors.toSet()))
                .reprints(bookEdition.getReprints().stream().map(bookReprintMapper::toDto)
                        .toList())
                .build();
    }
}
