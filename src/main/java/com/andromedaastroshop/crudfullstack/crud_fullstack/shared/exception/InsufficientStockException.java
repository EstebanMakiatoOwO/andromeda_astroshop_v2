package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}