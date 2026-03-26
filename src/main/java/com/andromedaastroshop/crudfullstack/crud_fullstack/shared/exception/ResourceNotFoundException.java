package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
