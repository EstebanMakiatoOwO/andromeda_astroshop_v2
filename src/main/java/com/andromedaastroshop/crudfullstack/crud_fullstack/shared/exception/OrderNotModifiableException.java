package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class OrderNotModifiableException extends RuntimeException {
    public OrderNotModifiableException(String message) {
        super(message);
    }
}