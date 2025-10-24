package com.example.demo.service;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.GenreRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookEditionDto;
import com.example.demo.dto.BookSummaryDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.book.BookReprint;
import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
import com.example.demo.exception.AuthorNotFoundException;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.DuplicateIsbnException;
import com.example.demo.mapper.BookEditionMapper;
import com.example.demo.mapper.BookMapper;
import com.example.demo.request.BookRequest;
import com.example.demo.entity.Author;
import com.example.demo.request.CreateEditionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookEditionRepository bookEditionRepository;
    private final BookEditionMapper bookEditionMapper;

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> getAllBooks(Pageable pageable){
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(bookMapper::toSummaryDto);
    }

    @Transactional
    public BookDto addBook(BookRequest request){
        Book book = Book.builder()
                .status(request.getStatus())
                .genres(new HashSet<>(genreRepository.findAllById(request.getGenreIds())))
                .build();

        //Create edition
        BookEdition edition = BookEdition.builder()
                .title(request.getTitle())
                .publishedYear(request.getPublishedYear())
                .description(request.getDescription())
                .editionNo(1)
                .isbn(request.getIsbn())
                .numberOfPages(request.getNumberOfPages())
                .translator(request.getTranslator())
                .dimension(request.getDimension())
                .authors(new HashSet<>(authorRepository.findAllById(request.getAuthorIds())))
                .build();

        //Create reprint
        BookReprint reprint = BookReprint.builder()
                .reprintNo(1)
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .discountPercentage(request.getDiscountPercentage())
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
                .build();

        // Link relationships (both sides)
        book.addEdition(edition);
        edition.addReprint(reprint);

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Transactional
    public BookEditionDto addEditionToBook(Long bookId, CreateEditionRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        // Calculate next edition number
        int nextEditionNo = book.getEditions().stream()
                .mapToInt(BookEdition::getEditionNo)
                .max()
                .orElse(0) + 1;

        // Validate ISBN uniqueness
        if (bookEditionRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException("ISBN already exists: " + request.getIsbn());
        }

        // Find authors
        Set<Author> authors = new HashSet<>(authorRepository.findAllById(request.getAuthorIds()));
        if (authors.size() != request.getAuthorIds().size()) {
            throw new AuthorNotFoundException("Some authors not found");
        }

        // Create edition
        BookEdition edition = BookEdition.builder()
                .book(book)
                .editionNo(nextEditionNo)
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .dimension(request.getDimension())
                .numberOfPages(request.getNumberOfPages())
                .publishedYear(request.getPublishedYear())
                .description(request.getDescription())
                .translator(request.getTranslator())
                .authors(authors)
                .build();

        // Create reprint
        BookReprint firstReprint = BookReprint.builder()
                .reprintNo(1)
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .reprintNotes(request.getReprintNotes())
                .discountPercentage(request.getDiscountPercentage())
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
                .build();

        // Link relationships (both sides)
        edition.addReprint(firstReprint);
        book.addEdition(edition);

        Book savedBook = bookRepository.save(book);

        // Check the saved edition
        BookEdition savedEdition = savedBook.getEditions().stream()
                .filter(e -> e.getIsbn().equals(request.getIsbn()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Edition not saved properly"));

        updateBookStatusIfNeeded(savedBook);

        return bookEditionMapper.toDto(savedEdition);
    }

    // Change status from out-of-stock to available
    private void updateBookStatusIfNeeded(Book book) {
        boolean hasAvailableStock = book.getEditions().stream()
                .flatMap(e -> e.getReprints().stream())
                .anyMatch(r -> r.getStock() > 0);

        if (hasAvailableStock && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
        }
    }
}
