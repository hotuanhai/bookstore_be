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
import com.example.demo.entity.genre.Genre;
import com.example.demo.enums.BookStatus;
import com.example.demo.exception.AuthorNotFoundException;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.DuplicateIsbnException;
import com.example.demo.exception.GenreNotFoundException;
import com.example.demo.mapper.BookEditionMapper;
import com.example.demo.mapper.BookMapper;
import com.example.demo.request.BookRequest;
import com.example.demo.entity.Author;
import com.example.demo.request.EditionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .name(request.getName())
                .isbn(request.getIsbn())
                .dimension(request.getDimension())
                .numberOfPages(request.getNumberOfPages())
                .publishedYear(request.getPublishedYear())
                .description(request.getDescription())
                .language(request.getLanguage())
                .translator(request.getTranslator())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .format(request.getFormat())
                .discountPercentage(request.getDiscountPercentage())
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
                .build();

        // Link relationships
        book.addEdition(edition);

        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Transactional
    public BookEditionDto addEditionToBook(Long bookId, EditionRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        // Validate ISBN uniqueness
        if (bookEditionRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException("ISBN already exists: " + request.getIsbn());
        }

        BookEdition edition = BookEdition.builder()
                .name(request.getName())
                .isbn(request.getIsbn())
                .dimension(request.getDimension())
                .numberOfPages(request.getNumberOfPages())
                .publishedYear(request.getPublishedYear())
                .description(request.getDescription())
                .language(request.getLanguage())
                .translator(request.getTranslator())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .format(request.getFormat())
                .discountPercentage(request.getDiscountPercentage())
                .discountStartDate(request.getDiscountStartDate())
                .discountEndDate(request.getDiscountEndDate())
                .build();
        // Link relationships
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

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> searchBooks(String keyword, Pageable pageable){
        return bookRepository.searchByTitleOrAuthor(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> getBooksByGenre(Long genreId, Pageable pageable){
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + genreId));
        return bookRepository.findByGenres_Id(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> getBooksByAuthor(Long authorId, Pageable pageable){
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with id: " + authorId));
        return bookRepository.findBooksByAuthorId(authorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> getDiscountedBooks(Pageable pageable){
        return bookRepository.findBooksWithActiveDiscount(LocalDateTime.now(), pageable);
    }

    @Transactional
    public BookDto updateBookStatus(Long bookId, BookStatus newStatus){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        book.setStatus(newStatus);
        Book updated = bookRepository.save(book);
        return bookMapper.toDto(updated);
    }

    @Transactional
    public void deleteBook(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        book.setStatus(BookStatus.DELETED);
        bookRepository.save(book);
        //bookRepository.delete(book);
    }

    // Change status from out-of-stock to available
    private void updateBookStatusIfNeeded(Book book) {
        boolean hasAvailableStock = book.getEditions().stream()
                .anyMatch(r -> r.getStock() > 0);

        if (hasAvailableStock && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
        }
    }
}
