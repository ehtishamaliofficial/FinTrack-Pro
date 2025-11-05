package com.fintrackpro.application.port.output;

import java.time.LocalDateTime;

public interface EmailServicePort {
    
    void sendVerificationEmail(String to, String username, String verificationToken);
    
    void sendPasswordResetEmail(String to, String username, String resetToken);
    
    void sendWelcomeEmail(String to, String username);

    /**
     * Send an email when a user logs in successfully.
     */
    void sendLoginSuccessEmail(String to, String username, String ipAddress, String userAgent, LocalDateTime loginTime);

    /**
     * Send an email when a user login fails (wrong password, etc.).
     */
    void sendLoginFailureEmail(String to, String username, String ipAddress, String userAgent, int failedAttempts, LocalDateTime lockUntil);
}
