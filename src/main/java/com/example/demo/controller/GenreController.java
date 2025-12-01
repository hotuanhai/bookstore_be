package com.example.demo.controller;

import com.example.demo.dto.GenreDto;
import com.example.demo.dto.MainGenreDto;
import com.example.demo.entity.genre.Genre;
import com.example.demo.entity.genre.MainGenre;
import com.example.demo.request.GenreRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.GenreService;
import com.example.demo.service.MainGenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(Long id){
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Main genre deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreDto>> renameGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreRequest request){
        GenreDto dto = genreService.renameGenre(id, request);

        return ResponseEntity.ok(ApiResponse.success(dto,"Genre updated successfully"));
    }

    @GetMapping("/main-genre/{id}")
    ResponseEntity<ApiResponse<Set<GenreDto>>> getGenreByMainGenreId(@PathVariable Long id){
        Set<GenreDto> genreDtos = genreService.findGenreByMainGenreId(id);

        return ResponseEntity.ok(ApiResponse.success(genreDtos,"Genres fetched successfully"));
    }

    @GetMapping
    ResponseEntity<ApiResponse<Set<GenreDto>>> getAllGenres(){
        Set<GenreDto> genreDtos = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success(genreDtos,"Genres fetched successfully"));
    }

    @PutMapping("/{genreId}/move/{targetMainGenreId}")
    public ResponseEntity<ApiResponse<Void>> moveGenre(
            @PathVariable Long genreId,
            @PathVariable Long targetMainGenreId) {
        genreService.moveGenre(genreId, targetMainGenreId);
        return ResponseEntity.ok(ApiResponse.success(null, "Genre moved successfully"));
    }

}
