package com.example.demo.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 30, message = "Display name must not exceed 30 characters")
    private String displayName;

    private String avatarUrl;

    @Size(max = 255, message = "Bio must not exceed 255 characters")
    private String bio;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String location;

    private String facebookUrl;
}
