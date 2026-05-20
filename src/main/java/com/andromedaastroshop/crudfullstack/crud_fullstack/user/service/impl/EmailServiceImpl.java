package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendVerificationEmail(String toEmail, String userName, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Confirma tu cuenta en Andromeda Astro Shop");

            String verifyUrl = baseUrl + "/api/v1/auth/verify?token=" + token;
            String html = buildEmailHtml(userName, verifyUrl);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }

    private String buildEmailHtml(String userName, String verifyUrl) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px;">
                  <div style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 8px; padding: 40px;">
                    <h2 style="color: #1a1a2e; margin-bottom: 8px;">¡Hola, %s!</h2>
                    <p style="color: #555; font-size: 15px; line-height: 1.6;">
                      Gracias por registrarte en <strong>Andromeda Astro Shop</strong>.
                      Solo falta un paso: confirma tu dirección de correo haciendo clic en el botón de abajo.
                    </p>
                    <div style="text-align: center; margin: 32px 0;">
                      <a href="%s"
                         style="background-color: #6c3fc5; color: #fff; text-decoration: none;
                                padding: 14px 32px; border-radius: 6px; font-size: 16px; font-weight: bold;">
                        Verificar mi cuenta
                      </a>
                    </div>
                    <p style="color: #999; font-size: 13px;">
                      Si no creaste esta cuenta, puedes ignorar este mensaje.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(userName, verifyUrl);
    }
}