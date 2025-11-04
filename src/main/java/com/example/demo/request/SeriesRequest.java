package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesRequest {
    @NotNull(message = "Series name is required")
    @NotBlank(message = "Series name cannot be blank")
    private String name;
    private String description;
    @NotNull(message = "Book IDs cannot be null")
    @NotEmpty(message = "Series must contain at least one book")
    private Set<Long> bookIds;
}
