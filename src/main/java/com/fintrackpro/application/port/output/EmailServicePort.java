package com.fintrackpro.application.port.output;

public interface EmailServicePort {
    
    void sendVerificationEmail(String to, String username, String verificationToken);
    
    void sendPasswordResetEmail(String to, String username, String resetToken);
    
    void sendWelcomeEmail(String to, String username);
}
