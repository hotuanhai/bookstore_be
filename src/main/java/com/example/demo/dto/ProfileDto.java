package com.example.demo.dto;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {
    private Long id;
    private Long userId;

    private String displayName;
    private String avatarUrl;
    private String bio;
    private LocalDate dateOfBirth;
    private String location;
    private String facebookUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
