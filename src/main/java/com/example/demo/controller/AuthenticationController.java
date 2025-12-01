package com.example.demo.controller;

import com.example.demo.dto.security.*;
import com.example.demo.entity.user.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.security.AuthenticationService;
import com.example.demo.service.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody RegisterUserDto registerUserDto) {
        User user = authenticationService.signup(registerUserDto);
        UserResponseDto userDto = UserResponseDto.builder()
                .role(user.getRole())
                .id(user.getId())
                .email(user.getUsername())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(userDto, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginUserDto dto) {
        LoginResponse response = authenticationService.login(dto);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful")
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok(ApiResponse
                    .success(null,"Account verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshDto dto) {
        try {
            LoginResponse response = authenticationService.refreshAccessToken(dto);
            return ResponseEntity
                    .ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Refresh token expired. Please login again."));
        } catch (JwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid refresh token."));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<String>> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity
                    .ok(ApiResponse.success(null, "Verification code sent successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
