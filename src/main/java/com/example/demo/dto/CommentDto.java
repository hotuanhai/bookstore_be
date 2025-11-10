package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;

    private Long bookId;

    private UserSummaryDto user;

    private String content;
}
