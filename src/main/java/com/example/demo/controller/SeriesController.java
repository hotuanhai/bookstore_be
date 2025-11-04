package com.example.demo.controller;

import com.example.demo.dto.SeriesDto;
import com.example.demo.request.SeriesRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {
    private final SeriesService seriesService;
    @PostMapping
    public ResponseEntity<ApiResponse<SeriesDto>> createSeries(@Valid @RequestBody SeriesRequest request) {
        SeriesDto series = seriesService.createSeries(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(series, "Series created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeriesDto>> updateSeries(
            @PathVariable Long id,
            @Valid @RequestBody SeriesRequest request) {
        SeriesDto series = seriesService.updateSeries(id, request);
        return ResponseEntity.ok(ApiResponse.success(series, "Series updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Series deleted successfully"));
    }

    @PostMapping("/{seriesId}/books")
    public ResponseEntity<ApiResponse<SeriesDto>> addBooksToSeries(
            @PathVariable Long seriesId,
            @RequestBody Set<Long> bookIds) {
        SeriesDto updatedSeries = seriesService.addBooksToSeries(seriesId, bookIds);
        return ResponseEntity.ok(ApiResponse.success(updatedSeries, "Books added to series successfully"));
    }

    @DeleteMapping("/{seriesId}/books/{bookId}")
    public ResponseEntity<ApiResponse<SeriesDto>> removeBookFromSeries(
            @PathVariable Long seriesId,
            @PathVariable Long bookId) {
        SeriesDto updatedSeries = seriesService.removeBookFromSeries(seriesId, bookId);
        return ResponseEntity.ok(ApiResponse.success(updatedSeries, "Book removed from series successfully"));
    }
}
