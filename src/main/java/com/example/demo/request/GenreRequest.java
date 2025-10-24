package com.example.demo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenreRequest {
    @NotNull(message = "Genre name is required")
    @NotBlank(message = "Genre name cannot be blank")
    private String name;
//
//    @NotNull(message = "Main genre is required")
//    @NotBlank(message = "Main genre cannot be blank")
//    private Long mainGenreId;
}
