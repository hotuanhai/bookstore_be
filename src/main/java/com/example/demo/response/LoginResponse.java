package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private long accessTokenExpiresIn;

    private String refreshToken;
    private long refreshTokenExpiresIn;
}