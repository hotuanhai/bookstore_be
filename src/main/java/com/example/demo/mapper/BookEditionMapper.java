package com.example.demo.mapper;

import com.example.demo.dto.bookEdition.BookEditionDto;
import com.example.demo.entity.book.BookEdition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookEditionMapper {
    public BookEditionDto toDto(BookEdition bookEdition){
        return BookEditionDto.builder()
                .id(bookEdition.getId())
                .name(bookEdition.getName())
                .isbn(bookEdition.getIsbn())
                .publisher(bookEdition.getPublisher())
                .publishedYear(bookEdition.getPublishedYear())
                .description(bookEdition.getDescription())
                .language(bookEdition.getLanguage())
                .format(bookEdition.getFormat())
                .price(bookEdition.getPrice())
                .stock(bookEdition.getStock())
                .imageUrl(bookEdition.getImageUrl())
                .discountPercentage(bookEdition.getDiscountPercentage())
                .discountStartDate(bookEdition.getDiscountStartDate())
                .discountEndDate(bookEdition.getDiscountEndDate())
                .status(bookEdition.getStatus())
                .build();
    }
}
