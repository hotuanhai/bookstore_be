package com.example.demo.controller;

import com.example.demo.dto.CommentDto;
import com.example.demo.request.CommentRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> addComment(@Valid @RequestBody CommentRequest request) {
        CommentDto dto = commentService.addComment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto, "Comment created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ) {
        CommentDto dto = commentService.updateComment(id, request.getContent());

        return ResponseEntity.ok(ApiResponse.success(dto,"Comment updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByUser(@PathVariable Long userId) {
        List<CommentDto> dtos = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(dtos,"Comments fetched successfully"));
    }

    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByBook(@PathVariable Long bookId) {
        List<CommentDto> dtos = commentService.getCommentsByBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(dtos,"Comments fetched successfully"));
    }
}
