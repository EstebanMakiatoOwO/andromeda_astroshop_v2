package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String userName, String token);
}
