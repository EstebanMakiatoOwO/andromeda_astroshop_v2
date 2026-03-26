package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
