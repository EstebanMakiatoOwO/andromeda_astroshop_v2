package com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception;

public class PaymentAlreadyProcessedException extends RuntimeException {
    public PaymentAlreadyProcessedException(String message) {
        super(message);
    }
}