package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistDto {
    private Long wishlistItemId;
    private Long bookEditionId;
    private LocalDateTime createdAt;
}
