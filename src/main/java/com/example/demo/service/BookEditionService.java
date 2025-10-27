package com.example.demo.service;

import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dto.BookEditionDto;
import com.example.demo.dto.BookReprintDto;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.book.BookReprint;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.EditionNotFoundException;
import com.example.demo.mapper.BookReprintMapper;
import com.example.demo.request.ReprintRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookEditionService {
    private final BookEditionRepository bookEditionRepository;
    private final BookReprintMapper bookReprintMapper;

    public BookReprintDto addReprintToEdition(Long editionId, ReprintRequest request){
        BookEdition edition = bookEditionRepository.findById(editionId)
                .orElseThrow(() -> new EditionNotFoundException("Edition not found with id: " + editionId));

        // Calculate next reprint number
        int nextReprintNo = edition.getReprints().stream()
                .mapToInt(BookReprint::getReprintNo)
                .max()
                .orElse(0) + 1;

        BookReprint reprint = BookReprint.builder()
                .reprintNo(nextReprintNo)
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .reprintNotes(request.getReprintNotes())
                .discountPercentage(request.getDiscountPercentage())
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
                .build();

        edition.addReprint(reprint);

        BookEdition saved = bookEditionRepository.save(edition);
        BookReprint savedReprint = saved.getReprints()
                .stream()
                .filter(r -> r.getReprintNo() == nextReprintNo)
                .findFirst()
                .orElse(reprint);

        return bookReprintMapper.toDto(savedReprint);
    }
}
