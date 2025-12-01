package com.example.demo.dto.security;

import lombok.*;

import java.time.LocalDate;

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
}
