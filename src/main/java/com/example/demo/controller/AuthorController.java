package com.example.demo.controller;

import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.AuthorSummaryDto;
import com.example.demo.dto.CommentDto;
import com.example.demo.request.AuthorRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorDto>> addAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorDto dto = authorService.addAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Author created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorDto>> updateAuthor(@PathVariable Long id,@Valid @RequestBody AuthorRequest request) {
        AuthorDto dto = authorService.updateAuthor(id, request);

        return ResponseEntity.ok(ApiResponse.success(dto,"Author updated successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<AuthorSummaryDto>>> searchAuthors(@RequestParam String keyword, Pageable pageable) {
        Page<AuthorSummaryDto> dtos = authorService.searchAuthors(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success(dtos,"Author searched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuthorSummaryDto>>> getAllAuthors(Pageable pageable) {
        Page<AuthorSummaryDto> dtos = authorService.getAllAuthors(pageable);

        return ResponseEntity.ok(ApiResponse.success(dtos,"Authors fetched successfully"));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<ApiResponse<AuthorDto>> getAuthorById(@PathVariable Long authorId) {
        AuthorDto dto = authorService.getAuthorById(authorId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Author fetched successfully"));
    }
}
