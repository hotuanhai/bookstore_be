package com.example.demo.controller;

import com.example.demo.dto.genre.GenreDto;
import com.example.demo.dto.MainGenreDto;
import com.example.demo.request.GenreRequest;
import com.example.demo.request.MainGenreRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.MainGenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/main-genres")
@RequiredArgsConstructor
public class MainGenreController {
    private final MainGenreService mainGenreService;

    @PostMapping
    public ResponseEntity<ApiResponse<MainGenreDto>> addMainGenre(
            @Valid @RequestBody MainGenreRequest request) {
        MainGenreDto dto = mainGenreService.addMainGenre(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto,"Main genre created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MainGenreDto>> getMainGenreById(@PathVariable Long id) {
        MainGenreDto dto = mainGenreService.getMainGenreById(id);

        return ResponseEntity.ok(ApiResponse.success(dto,"Main genre fetched successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MainGenreDto>> renameMainGenre(
            @PathVariable Long id,
            @Valid @RequestBody MainGenreRequest request) {
        MainGenreDto dto = mainGenreService.renameMainGenre(id, request);

        return ResponseEntity.ok(ApiResponse.success(dto,"Main genre updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMainGenre(@PathVariable Long id) {
        mainGenreService.deleteMainGenre(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Main genre deleted successfully"));
    }

    @PostMapping("/{id}/genres")
    public ResponseEntity<ApiResponse<GenreDto>> addGenreToMainGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreRequest request){
        GenreDto dto = mainGenreService.addGenreToMainGenre(id, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(dto,"Genre added to main genre successfully"));
    }

    @GetMapping ResponseEntity<ApiResponse<List<MainGenreDto>>> getAllMainGenre(){
        List<MainGenreDto> dtos = mainGenreService.getAllMainGenre();
        return ResponseEntity.ok(ApiResponse.success(dtos,"All main genre fetched successfully"));
    }
}
