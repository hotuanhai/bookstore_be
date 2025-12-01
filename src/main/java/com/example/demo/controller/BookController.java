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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<Page<BookDto>>> getAllBooksWithDetails(Pageable pageable) {
        Page<BookDto> books = bookService.getAllBooksWithDetails(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(books,"All books fetched successfully"));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookDto>> getBook(@PathVariable Long bookId) {
        BookDto dto = bookService.getBookById(bookId);
        return ResponseEntity.ok(
                ApiResponse.success(dto, "Book deleted successfully"));
    }

    @GetMapping("/latest")
    ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getLatestBooks(
            @RequestParam(required = false, defaultValue = "") String countries,
            @RequestParam(required = false) String sort, // e.g., "price,asc" or "latest,desc"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Set<String> countrySet = Arrays.stream(countries.split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
        //handle sort
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            String direction = sortParams.length > 1 ? sortParams[1] : "asc";
            if ("latest".equals(field)) field = "b.id";
            else if ("price".equals(field)) field = "e.price";
            sortObj = direction.equalsIgnoreCase("desc")
                    ? Sort.by(field).descending()
                    : Sort.by(field).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<BookSummaryDto> dtos = bookService.getLatestBooks(countrySet, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(dtos, "Latest books fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookDto>> addBook(
            @Valid @RequestBody BookRequest request) {
        BookDto dto = bookService.addBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Book created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable Long id, @RequestBody BookRequest request) {
        BookDto updated = bookService.updateBook(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(updated,"Book updated successfully"));
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> searchBooks(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String countries,
            @RequestParam(required = false) String sort, // e.g., "price,asc" or "latest,desc"
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Set<String> countrySet = Arrays.stream(countries.split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
        //handle sort
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            String direction = sortParams.length > 1 ? sortParams[1] : "asc";
            if ("latest".equals(field)) {
                field = "b.id";
            } else if ("price".equals(field)) {
                field = "e.price";
            } else if ("newest".equals(field)) {
                field = "e.publishedYear";
            }
            sortObj = direction.equalsIgnoreCase("desc")
                    ? Sort.by(field).descending()
                    : Sort.by(field).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<BookSummaryDto> books = bookService.searchBooks(keyword,countrySet, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(books, "Books fetched successfully"));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getBooksByGenre(
            @PathVariable Long genreId, Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getBooksByGenre(genreId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(books, "Books by genre fetched successfully"));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getBooksByAuthor(
            @PathVariable Long authorId, Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getBooksByAuthor(authorId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(books, "Books by author fetched successfully"));
    }

    @GetMapping("/discounted")
    public ResponseEntity<ApiResponse<Page<BookSummaryDto>>> getDiscountedBooks(Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getDiscountedBooks(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(books, "Discounted books fetched successfully"));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Book deleted successfully"));
    }
}
