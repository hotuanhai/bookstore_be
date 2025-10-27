package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookEditionDto;
import com.example.demo.dto.BookSummaryDto;
import com.example.demo.request.BookRequest;
import com.example.demo.request.EditionRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookDto>> addBook(
            @Valid @RequestBody BookRequest request) {
        BookDto dto = bookService.addBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Book created successfully"));
    }

    @PostMapping("/{bookId}/editions")
    public ResponseEntity<ApiResponse<BookEditionDto>> addEditionToBook(
            @PathVariable Long bookId,
            @Valid @RequestBody EditionRequest request) {
        BookEditionDto dto = bookService.addEditionToBook(bookId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Edition added successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> searchBooks(
            @RequestParam String keyword, Pageable pageable) {
        Page<BookSummaryDto> books = bookService.searchBooks(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(books, "Books fetched successfully"));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getBooksByGenre(
            @PathVariable Long genreId, Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getBooksByGenre(genreId, pageable);

        return ResponseEntity.ok(ApiResponse.success(books, "Books by genre fetched successfully"));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getBooksByAuthor(
            @PathVariable Long authorId, Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getBooksByAuthor(authorId, pageable);

        return ResponseEntity.ok(ApiResponse.success(books, "Books by author fetched successfully"));
    }

    @GetMapping("/discounted")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getDiscountedBooks(Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getDiscountedBooks(pageable);

        return ResponseEntity.ok(ApiResponse.success(books, "Discounted books fetched successfully"));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null, "Book deleted successfully"));
    }
}
