package com.example.demo.exception;

public class EditionNotFoundException extends RuntimeException {
    public EditionNotFoundException(String message) {
        super(message);
    }
}
