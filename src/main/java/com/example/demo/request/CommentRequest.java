package com.example.demo.request;

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
public class CommentRequest {
    private Long bookId;

    private Long userId;

    @NotNull(message = "Content is required")
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
