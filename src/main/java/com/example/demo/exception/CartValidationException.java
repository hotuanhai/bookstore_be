package com.example.demo.exception;

public class CartValidationException extends RuntimeException {
  public CartValidationException(String message) {
    super(message);
  }
}
