package com.fintrackpro.infrastructure.adapter.output.email;

import com.fintrackpro.application.port.output.EmailServicePort;
import com.fintrackpro.infrastructure.util.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailServicePort {

    private final JavaMailSender mailSender;
    private final MessageUtil messageUtil;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String to, String username, String verificationToken) {
        try {
            String subject = messageUtil.getMessage("email.verification.subject");
            String verificationLink = frontendUrl + "/api/v1/auth/verify-email?token=" + verificationToken;
            
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("subject", subject);
            context.setVariable("headerTitle", messageUtil.getMessage("email.verification.header"));
            context.setVariable("headerTheme", "primary");
            context.setVariable("footerText", messageUtil.getMessage("email.footer"));
            context.setVariable("baseUrl", frontendUrl);
            context.setVariable("contentTemplate", "email/verification-email");
            
            String htmlContent = templateEngine.process("email/email-base", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Verification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String username, String resetToken) {
        try {
            String subject = messageUtil.getMessage("email.password.reset.subject");
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("resetLink", resetLink);
            context.setVariable("subject", subject);
            context.setVariable("headerTitle", messageUtil.getMessage("email.password.reset.header"));
            context.setVariable("headerTheme", "danger");
            context.setVariable("footerText", messageUtil.getMessage("email.footer"));
            context.setVariable("baseUrl", frontendUrl);
            context.setVariable("contentTemplate", "email/password-reset-email");
            
            String htmlContent = templateEngine.process("email/email-base", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Password reset email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        try {
            String subject = messageUtil.getMessage("email.welcome.subject");
            String loginLink = frontendUrl + "/login";
            
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("loginLink", loginLink);
            context.setVariable("subject", subject);
            context.setVariable("headerTitle", messageUtil.getMessage("email.welcome.header"));
            context.setVariable("headerTheme", "success");
            context.setVariable("footerText", messageUtil.getMessage("email.footer"));
            context.setVariable("baseUrl", frontendUrl);
            context.setVariable("contentTemplate", "email/welcome-email");
            
            String htmlContent = templateEngine.process("email/email-base", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", to, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    /**
     * Send a generic email using custom template
     * @param to Recipient email address
     * @param subject Email subject
     * @param title Email title/heading
     * @param content HTML content
     * @param buttonText Optional button text (can be null)
     * @param buttonLink Optional button link (can be null)
     * @param buttonStyle Button style class (primary, success, danger, warning)
     * @param infoMessage Optional info box message (can be null)
     * @param headerTheme Header theme (primary, success, danger, warning)
     */
    public void sendGenericEmail(String to, String subject, String title, String content, 
                                  String buttonText, String buttonLink, String buttonStyle, 
                                  String infoMessage, String headerTheme) {
        try {
            Context context = new Context();
            context.setVariable("title", title);
            context.setVariable("content", content);
            context.setVariable("buttonText", buttonText);
            context.setVariable("buttonLink", buttonLink);
            context.setVariable("buttonStyle", buttonStyle);
            context.setVariable("infoMessage", infoMessage);
            context.setVariable("subject", subject);
            context.setVariable("headerTitle", title);
            context.setVariable("headerTheme", headerTheme);
            context.setVariable("footerText", messageUtil.getMessage("email.footer"));
            context.setVariable("baseUrl", frontendUrl);
            context.setVariable("contentTemplate", "email/generic-email");
            
            String htmlContent = templateEngine.process("email/email-base", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Generic email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send generic email to: {}", to, e);
            throw new RuntimeException("Failed to send generic email", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        mailSender.send(message);
    }

    private String buildVerificationEmailBody_OLD_DEPRECATED(String username, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4F46E5; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #4F46E5; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <h2>%s</h2>
                        <p>%s</p>
                        <p>%s</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">%s</a>
                        </div>
                        <p>%s</p>
                        <p style="word-break: break-all; color: #666; font-size: 12px;">%s</p>
                        <p>%s</p>
                    </div>
                    <div class="footer">
                        <p>%s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                messageUtil.getMessage("email.verification.header"),
                messageUtil.getMessage("email.verification.greeting", username),
                messageUtil.getMessage("email.verification.message"),
                messageUtil.getMessage("email.verification.instruction"),
                verificationLink,
                messageUtil.getMessage("email.verification.button"),
                messageUtil.getMessage("email.verification.alternative"),
                verificationLink,
                messageUtil.getMessage("email.verification.expiry"),
                messageUtil.getMessage("email.footer")
            );
    }

    private String buildPasswordResetEmailBody(String username, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #DC2626; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #DC2626; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <h2>%s</h2>
                        <p>%s</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">%s</a>
                        </div>
                        <p>%s</p>
                        <p>%s</p>
                    </div>
                    <div class="footer">
                        <p>%s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                messageUtil.getMessage("email.password.reset.header"),
                messageUtil.getMessage("email.password.reset.greeting", username),
                messageUtil.getMessage("email.password.reset.message"),
                resetLink,
                messageUtil.getMessage("email.password.reset.button"),
                messageUtil.getMessage("email.password.reset.expiry"),
                messageUtil.getMessage("email.password.reset.ignore"),
                messageUtil.getMessage("email.footer")
            );
    }

    private String buildWelcomeEmailBody(String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #10B981; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <h2>%s</h2>
                        <p>%s</p>
                        <p>%s</p>
                    </div>
                    <div class="footer">
                        <p>%s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                messageUtil.getMessage("email.welcome.header"),
                messageUtil.getMessage("email.welcome.greeting", username),
                messageUtil.getMessage("email.welcome.message"),
                messageUtil.getMessage("email.welcome.closing"),
                messageUtil.getMessage("email.footer")
            );
    }
}
