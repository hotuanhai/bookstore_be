package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
    @NotNull(message = "Author name is required")
    @NotBlank(message = "Author name cannot be blank")
    private String name;
    private String imageUrl;
    private String description;
    private String nationality;
}
