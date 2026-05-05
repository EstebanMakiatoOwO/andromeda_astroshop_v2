package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class ProductNotPurchasedException extends RuntimeException {
    public ProductNotPurchasedException(String message) {
        super(message);
    }
}