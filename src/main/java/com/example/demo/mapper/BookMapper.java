package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookSummaryDto;
import com.example.demo.entity.Author;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.book.BookReprint;
import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    private final AuthorMapper authorMapper;

    public BookMapper(@Lazy AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public BookSummaryDto toSummaryDto(Book book) {
        //Get latest edition
        BookEdition latestEdition = getLatestEdition(book);
        //Get best reprint (prefer available, then latest)
        BookReprint bestReprint = getBestReprint(latestEdition);

        return BookSummaryDto.builder()
                .id(book.getId())
                .title(latestEdition.getTitle())
                .price(bestReprint.getPrice())
                .imageUrl(bestReprint.getImageUrl())
                .status(bestReprint.getStatus())
                .discountPercentage(bestReprint.getDiscountPercentage())
                .discountStartDate(bestReprint.getDiscountStartDate())
                .discountEndDate(bestReprint.getDiscountEndDate())
                .build();
    }

    public BookDto toDto(Book book){
        //Get latest edition
        BookEdition latestEdition = getLatestEdition(book);
        //Get best reprint (prefer available, then latest)
        BookReprint bestReprint = getBestReprint(latestEdition);

        return BookDto.builder()
                .id(book.getId())
                .title(latestEdition.getTitle())
                .authors(latestEdition.getAuthors().stream().map(authorMapper::toSummaryDto).collect(Collectors.toSet()))
                .genres(book.getGenres().stream().map(Genre::getName).collect(Collectors.toSet()))
                .imageUrl(bestReprint.getImageUrl())
                .price(bestReprint.getPrice())
                .numberOfPages(latestEdition.getNumberOfPages())
                .description(latestEdition.getDescription())
                .editionNo(latestEdition.getEditionNo())
                .reprintNo(bestReprint.getReprintNo())
                .isbn(latestEdition.getIsbn())
                .stock(bestReprint.getStock())
                .publishedYear(latestEdition.getPublishedYear())
                .dimension(latestEdition.getDimension())
                .status(book.getStatus()) ////////idk
                .translator(latestEdition.getTranslator())
                .reprintNotes(bestReprint.getReprintNotes())
                .discountPercentage(bestReprint.getDiscountPercentage())
                .discountStartDate(bestReprint.getDiscountStartDate())
                .discountEndDate(bestReprint.getDiscountEndDate())
                .build();
    }

    /// utils
    private BookReprint getBestReprint(BookEdition edition) {
        //Get latest AVAILABLE reprint
        Optional<BookReprint> availableReprint = edition.getReprints().stream()
                .filter(r -> r.getStatus() == BookStatus.AVAILABLE)
                .max(Comparator.comparing(BookReprint::getReprintNo));

        //If exist latest AVAILABLE reprint -> return
        //else return latest reprint regardless of status
        return availableReprint.orElseGet(() -> edition.getReprints().stream()
                .max(Comparator.comparing(BookReprint::getReprintNo))
                .orElse(null));
    }

    private BookEdition getLatestEdition(Book book) {
        return book.getEditions().stream()
                .max(Comparator.comparing(BookEdition::getEditionNo))
                .orElse(null);
    }
}
