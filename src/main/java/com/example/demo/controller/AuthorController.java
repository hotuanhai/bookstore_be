package com.example.demo.controller;

import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.AuthorSummaryDto;
import com.example.demo.request.AuthorRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<ApiResponse<AuthorDto>> addAuthor(@RequestBody AuthorRequest request) {
        AuthorDto dto = authorService.addAuthor(request);

        ApiResponse<AuthorDto> response = new ApiResponse<>();
        response.setStatus(0); // 0 = success
        response.setMessage("Author added successfully");
        response.setData(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorDto>> updateAuthor(@PathVariable Long id, @RequestBody AuthorRequest request) {
        AuthorDto dto = authorService.updateAuthor(id, request);

        ApiResponse<AuthorDto> response = new ApiResponse<>();
        response.setStatus(0); // 0 = success
        response.setMessage("Author added successfully");
        response.setData(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    public Page<AuthorSummaryDto> searchAuthors(@RequestParam String keyword, Pageable pageable) {
        return authorService.searchAuthors(keyword, pageable);
    }

    @GetMapping
    public Page<AuthorSummaryDto> getAllAuthors(Pageable pageable) {
        return authorService.getAllAuthors(pageable);
    }
}
