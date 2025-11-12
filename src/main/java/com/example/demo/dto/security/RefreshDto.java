package com.example.demo.dto.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDto {
    private String email;
    private String refreshToken;
}