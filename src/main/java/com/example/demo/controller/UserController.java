package com.example.demo.controller;

import com.example.demo.dto.security.UserResponseDto;
import com.example.demo.entity.user.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserResponseDto dto = UserResponseDto.builder()
                .role(currentUser.getRole())
                .id(currentUser.getId())
                .email(currentUser.getUsername())
                .build();
        return ResponseEntity.ok(ApiResponse.success(dto, "User fetched successfully"));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> allUsers() {
        List<User> users = userService.allUsers();
        List<UserResponseDto> dtos = users.stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getUsername())
                        .role(user.getRole())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success(dtos, "All users fetched successfully")
        );
    }
}
