package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookSummaryDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    private final AuthorMapper authorMapper;
    private final BookEditionMapper bookEditionMapper;

    public BookMapper(@Lazy AuthorMapper authorMapper,BookEditionMapper bookEditionMapper) {
        this.authorMapper = authorMapper;
        this.bookEditionMapper = bookEditionMapper;
    }

    public BookSummaryDto toSummaryDto(Book book) {
        BookEdition cheapestEdition = book.getEditions().stream()
                .filter(e -> e.getStatus() == BookStatus.AVAILABLE)
                .min(Comparator.comparing(BookEdition::getPrice))
                .orElseGet(() ->
                        book.getEditions().stream()
                                .min(Comparator.comparing(BookEdition::getPrice))
                                .orElse(null)
                );

        return BookSummaryDto.builder()
                .id(book.getId())
                .editionId(cheapestEdition.getId())
                .title(book.getTitle())
                .price(cheapestEdition.getPrice())
                .imageUrl(cheapestEdition.getImageUrl())
                .status(cheapestEdition.getStatus())
                .discountPercentage(cheapestEdition.getDiscountPercentage())
                .discountStartDate(cheapestEdition.getDiscountStartDate())
                .discountEndDate(cheapestEdition.getDiscountEndDate())
                .build();
    }

    public BookDto toDto(Book book){
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authors(book.getAuthors().stream()
                        .map(authorMapper::toSummaryDto).collect(Collectors.toSet()))
                .genres(book.getGenres().stream().map(Genre::getName).collect(Collectors.toSet()))
                .status(book.getStatus())
                .seriesId(book.getSeries() == null ? null : book.getSeries().getId())
                .seriesName(book.getSeries() == null ? null : book.getSeries().getName())
                .bookEditionDtos(book.getEditions() == null ? null : book.getEditions().stream()
                        .map(bookEditionMapper::toDto).collect(Collectors.toSet()))
                .build();
    }
}
