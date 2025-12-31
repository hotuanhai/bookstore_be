package com.example.demo.controller;

import com.example.demo.dto.BookEditionDto;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.user.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookEditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/book-editions")
@RequiredArgsConstructor
public class BookEditionController {

    private final BookEditionService editionService;

    @PutMapping("/{bookId}/editions/{editionId}")
    public ResponseEntity<ApiResponse<BookEditionDto>> updateEdition(
            @PathVariable Long bookId,
            @PathVariable Long editionId,
            @RequestBody BookEdition updatedEdition,
            @AuthenticationPrincipal User user
    ) {
        BookEditionDto dto =
                editionService.updateBookEdition(bookId, editionId, updatedEdition, user);
        return ResponseEntity.ok(ApiResponse.success(dto, "Edition updated successfully"));
    }
}
