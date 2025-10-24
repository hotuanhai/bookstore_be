package com.example.demo.mapper;

import com.example.demo.dto.BookReprintDto;
import com.example.demo.entity.book.BookReprint;
import org.springframework.stereotype.Component;

@Component
public class BookReprintMapper {
    public BookReprintDto toDto(BookReprint reprint){
        return BookReprintDto.builder()
                .id(reprint.getId())
                .editionId(reprint.getEdition().getId())
                .reprintNo(reprint.getReprintNo())
                .price(reprint.getPrice())
                .stock(reprint.getStock())
                .imageUrl(reprint.getImageUrl())
                .reprintNotes(reprint.getReprintNotes())
                .discountPercentage(reprint.getDiscountPercentage())
                .discountStartDate(reprint.getDiscountStartDate())
                .discountEndDate(reprint.getDiscountEndDate())
                .status(reprint.getStatus())
                .build();
    }
}
