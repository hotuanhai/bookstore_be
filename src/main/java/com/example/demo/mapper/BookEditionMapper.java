package com.example.demo.mapper;

import com.example.demo.dto.BookEditionDto;
import com.example.demo.entity.book.BookEdition;
import org.springframework.stereotype.Component;

@Component
public class BookEditionMapper {
    public BookEditionDto toDto(BookEdition bookEdition){
        return BookEditionDto.builder().build();
    }
}
