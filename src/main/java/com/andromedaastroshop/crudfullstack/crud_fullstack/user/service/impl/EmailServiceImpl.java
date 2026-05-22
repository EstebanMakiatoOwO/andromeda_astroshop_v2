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

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.from}")
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

            String verifyUrl = frontendUrl + "/verify-email?token=" + token;
            String html = buildEmailHtml(userName, verifyUrl);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }

    private String buildEmailHtml(String userName, String verifyUrl) {
        String template = """
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html dir="ltr" lang="en">
  <head>
    <meta content="width=device-width" name="viewport" />
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <meta name="x-apple-disable-message-reformatting" />
    <meta content="IE=edge" http-equiv="X-UA-Compatible" />
    <meta name="x-apple-disable-message-reformatting" />
    <meta content="telephone=no,address=no,email=no,date=no,url=no" name="format-detection" />
    <style>
      * { box-sizing: border-box; }
      body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
      table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
      img { -ms-interpolation-mode: bicubic; border: 0; display: block; }
      a { color: #7C5CE8; text-decoration: none; }
      a:hover { text-decoration: underline; }
      @media only screen and (max-width: 600px) {
        .email-wrapper { width: 100% !important; }
        .email-pad     { padding: 24px 16px !important; }
      }
    </style>
  </head>
  <body style="background-color:#ffffff">
    <table border="0" width="100%" cellpadding="0" cellspacing="0" role="presentation" align="center">
      <tbody><tr><td style="background-color:#ffffff">
        <table align="left" width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation"
          style="max-width:600px;width:100%;color:#000000;background-color:#ffffff">
          <tbody><tr style="width:100%"><td>
            <table align="center" width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation">
              <tbody><tr style="margin:0;padding:0"><td data-id="__react-email-column" style="margin:0;padding:0;background-color:#ffffff">
                <table align="left" width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation"
                  style="max-width:600px;width:100%;color:#000000;background-color:#ffffff">
                  <tbody><tr style="width:100%"><td>
                    <table width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation"
                      style="background-color:#0A0A14">
                      <tbody><tr style="margin:0;padding:0">
                        <td align="center" data-id="__react-email-column" style="margin:0;padding:40px 16px">
                          <table width="520" border="0" cellpadding="0" cellspacing="0" role="presentation"
                            class="email-wrapper" style="max-width:520px;width:100%">
                            <tbody>
                              <tr style="margin:0;padding:0">
                                <td align="center" data-id="__react-email-column"
                                  style="margin:0;padding:36px 40px 28px;background-color:#10101E;border-radius:16px 16px 0 0;border:1px solid #1E1E30;border-bottom:none">
                                  <p style="margin:0;padding:0;font-family:'Courier New',Courier,monospace;font-size:11px;color:#7C5CE8;letter-spacing:0.12em;text-transform:uppercase">// andromeda</p>
                                  <h1 style="margin:6px 0 0;padding:0;font-size:22px;font-weight:700;color:#EAEAF5;letter-spacing:-0.02em">Astroshop</h1>
                                  <div style="margin:20px auto 0;width:40px;height:2px;background:linear-gradient(90deg,#7C5CE8,#A78BFA);border-radius:2px"></div>
                                </td>
                              </tr>
                              <tr style="margin:0;padding:0">
                                <td class="email-pad" align="center" data-id="__react-email-column"
                                  style="margin:0;padding:40px;background-color:#10101E;border:1px solid #1E1E30;border-top:none;border-bottom:none">
                                  <div style="margin:0 auto 24px;width:64px;height:64px;background-color:#1A1A2E;border:1px solid #2A2A45;border-radius:50%;text-align:center;line-height:64px;font-size:28px">✉️</div>
                                  <h2 style="margin:0 0 12px;padding:0;font-size:20px;font-weight:700;color:#EAEAF5">Confirma tu correo</h2>
                                  <p style="margin:0 0 8px;padding:0;font-size:14px;color:#8888AA;line-height:1.6">Hola <strong>{{CUSTOMER_NAME}}</strong>,</p>
                                  <p style="margin:0 0 32px;padding:0;font-size:14px;color:#8888AA;line-height:1.6">
                                    Gracias por registrarte en <strong>Andromeda Astroshop</strong>. Solo falta un paso: haz clic en el botón de abajo para verificar tu correo y activar tu cuenta.
                                  </p>
                                  <table border="0" cellpadding="0" cellspacing="0" role="presentation"
                                    style="margin:0 auto 32px">
                                    <tbody><tr style="margin:0;padding:0">
                                      <td align="center" data-id="__react-email-column"
                                        style="margin:0;padding:0;background-color:#7C5CE8;border-radius:10px">
                                        <a href="{{VERIFY_URL}}" rel="noopener noreferrer nofollow" target="_blank"
                                          style="color:#FFFFFF;text-decoration:none;display:inline-block;padding:14px 36px;font-size:15px;font-weight:600;border-radius:10px">
                                          Confirmar mi correo →
                                        </a>
                                      </td>
                                    </tr></tbody>
                                  </table>
                                  <p style="margin:0 0 8px;padding:0;font-size:12px;color:#55556A">Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                                  <p style="margin:0;padding:0;font-size:11px;color:#7C5CE8;word-break:break-all">
                                    <a href="{{VERIFY_URL}}" rel="noopener noreferrer nofollow" target="_blank"
                                      style="color:#7C5CE8;text-decoration:underline"><u>{{VERIFY_URL}}</u></a>
                                  </p>
                                </td>
                              </tr>
                              <tr style="margin:0;padding:0">
                                <td data-id="__react-email-column"
                                  style="margin:0;padding:20px 40px;background-color:#0D0D1C;border:1px solid #1E1E30;border-top:none;border-bottom:none">
                                  <table width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation">
                                    <tbody><tr style="margin:0;padding:0">
                                      <td data-id="__react-email-column" style="margin:0;padding:0;padding-top:1px">
                                        <span style="color:#8888AA;font-size:12px;font-weight:bold">ℹ</span>
                                      </td>
                                      <td data-id="__react-email-column" style="margin:0;padding:0;padding-left:10px">
                                        <p style="margin:0;padding:0;font-size:12px;color:#55556A;line-height:1.6">
                                          Si no creaste una cuenta en Andromeda Astroshop, ignora este correo. El link expira en <strong>24 horas</strong>.
                                        </p>
                                      </td>
                                    </tr></tbody>
                                  </table>
                                </td>
                              </tr>
                              <tr style="margin:0;padding:0">
                                <td align="center" data-id="__react-email-column"
                                  style="margin:0;padding:24px 40px 28px;background-color:#080812;border:1px solid #1E1E30;border-top:none;border-radius:0 0 16px 16px">
                                  <p style="margin:0 0 12px;font-family:'Courier New',Courier,monospace;font-size:11px;color:#44445A;letter-spacing:0.1em">// andromeda astroshop · 2026</p>
                                  <table border="0" cellpadding="0" cellspacing="0" role="presentation" style="margin:0 auto 16px">
                                    <tbody><tr style="margin:0;padding:0">
                                      <td data-id="__react-email-column" style="margin:0;padding:0 10px">
                                        <a href="https://andromedaastroshop.com/terminos" rel="noopener noreferrer nofollow" target="_blank"
                                          style="color:#55556A;text-decoration:none;font-size:11px">Términos</a>
                                      </td>
                                      <td data-id="__react-email-column" style="margin:0;padding:0 10px;border-left:1px solid #2A2A45">
                                        <a href="https://andromedaastroshop.com/privacidad" rel="noopener noreferrer nofollow" target="_blank"
                                          style="color:#55556A;text-decoration:none;font-size:11px">Privacidad</a>
                                      </td>
                                      <td data-id="__react-email-column" style="margin:0;padding:0 10px;border-left:1px solid #2A2A45">
                                        <a href="mailto:legal@andromedaastroshop.com" rel="noopener noreferrer nofollow" target="_blank"
                                          style="color:#55556A;text-decoration:none;font-size:11px">Contacto</a>
                                      </td>
                                    </tr></tbody>
                                  </table>
                                  <p style="margin:0;font-size:11px;color:#44445A;line-height:1.6">
                                    Recibiste este correo porque alguien se registró con esta dirección.<br />Si no fuiste tú, simplemente ignóralo.
                                  </p>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </td>
                      </tr></tbody>
                    </table>
                  </td></tr></tbody>
                </table>
              </td></tr></tbody>
            </table>
          </td></tr></tbody>
        </table>
      </td></tr></tbody>
    </table>
  </body>
</html>
""";
        return template
                .replace("{{CUSTOMER_NAME}}", userName)
                .replace("{{VERIFY_URL}}", verifyUrl);
    }
}