package com.example.demo.config;

public class SecurityConstants {
    public static final String[] PUBLIC_GET = {
            "/api/authors/**",
            "/api/blog/**",
            "/api/books/**",
            "/api/genres/**",
            "/api/main-genres/**",
            "/api/user/**"
    };
    public static final String[] PUBLIC_POST = {
            "/api/authors/**",
            "/api/blog/**",
            "/api/books/**",
            "/api/genres/**",
            "/api/main-genres/**",
            "/api/user/**"
    };
    public static final String[] PUBLIC_PUT = {
            "/api/authors/**",
            "/api/blog/**",
            "/api/books/**",
            "/api/genres/**",
            "/api/main-genres/**",
            "/api/user/**"
    };
}

