package com.example.demo.dto.security;

import com.example.demo.enums.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private Role role;
}
