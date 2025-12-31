package com.example.demo.service;

import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookEditionDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.user.User;
import com.example.demo.mapper.BookEditionMapper;
import com.example.demo.enums.StockReason;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookEditionService {
    private final BookEditionMapper bookEditionMapper;
    private final BookRepository bookRepository;
    private final BookEditionRepository editionRepository;
    private final StockService stockService;

    @Transactional
    public BookEditionDto updateBookEdition(Long bookId, Long editionId,
                                            BookEdition updatedEdition, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookEdition edition = editionRepository.findById(editionId)
                .orElseThrow(() -> new RuntimeException("Edition not found"));

        if (!edition.getBook().getId().equals(book.getId())) {
            throw new RuntimeException("Edition does not belong to book");
        }

        // Update fields
        edition.setName(updatedEdition.getName());
        edition.setFormat(updatedEdition.getFormat());
        edition.setIsbn(updatedEdition.getIsbn());
        edition.setPublishedYear(updatedEdition.getPublishedYear());
        edition.setPublisher(updatedEdition.getPublisher());
        edition.setDescription(updatedEdition.getDescription());
        edition.setLanguage(updatedEdition.getLanguage());
        edition.setPrice(updatedEdition.getPrice());
//        edition.setStock(updatedEdition.getStock());
        edition.setDiscountPercentage(updatedEdition.getDiscountPercentage());
        edition.setDiscountStartDate(updatedEdition.getDiscountStartDate());
        edition.setDiscountEndDate(updatedEdition.getDiscountEndDate());
        edition.setStatus(updatedEdition.getStatus());

        BookEdition saved = editionRepository.save(edition);

        updateStock(edition,updatedEdition.getStock(), user);
        return bookEditionMapper.toDto(saved);
    }

    private void updateStock(BookEdition edition,int newStock, User user){
        int oldStock = edition.getStock();
        if(oldStock == newStock) return;

        int difference = newStock - oldStock;

        if (difference > 0) {
            stockService.addStock(
                    edition.getId(),
                    difference,
                    StockReason.ADJUSTMENT_INCREASE,
                    "MANUAL-UPDATE",
                    "Stock adjusted from " + oldStock + " to " + newStock,
                    user
            );
        } else {
            stockService.removeStock(
                    edition.getId(),
                    Math.abs(difference),
                    StockReason.ADJUSTMENT_DECREASE,
                    "MANUAL-UPDATE",
                    "Stock adjusted from " + oldStock + " to " + newStock,
                    user
            );
        }
    }
}

