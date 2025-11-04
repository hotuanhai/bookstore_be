package com.example.demo.exception;

public class SeriesNotFoundException extends RuntimeException {
    public SeriesNotFoundException(String message) {
        super(message);
    }
}
