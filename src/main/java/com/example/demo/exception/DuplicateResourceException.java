package com.example.demo.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String bookAlreadyInWishlist) {
        super(bookAlreadyInWishlist);
    }
}
