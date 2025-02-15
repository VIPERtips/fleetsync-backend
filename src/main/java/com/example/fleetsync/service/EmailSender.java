package com.example.fleetsync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender javaMailSender;
    
    private static final String SUPPORT_EMAIL = "noreply@fleetsync.com";
    private static final String SUPPORT_LINK = "https://fleet-sync.vercel.app";
    


    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SUPPORT_EMAIL);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void sendPasswordResetEmail(String userEmail, String username, String resetToken) {
        String subject = "Fleet Sync - Password Reset Request";
        String resetLink = SUPPORT_LINK + "/reset-password?token=" + resetToken;

        String body = String.format(
            "Dear %s,\n\n" +
            "We received a request to reset your Fleet Sync password. If you initiated this request, please use the link below to set a new password:\n\n" +
            "%s\n\n" +
            "This link will expire in 24 hours for security reasons. If you did not request a password reset, please ignore this email or contact our support team.\n\n" +
            "Best regards,\n" +
            "Fleet Sync Support Team\n" +
            "%s",
            username, resetLink, SUPPORT_LINK
        );

        sendEmail(userEmail, subject, body);
    }
    
    public void sendAdminNotification(String userEmail) {
        String subject = "Fleet Sync - Password Reset Requested";
        String body = String.format(
            "An account password reset has been requested for the following user:\n\n" +
            "- Email: %s\n\n" +
            "If this was not initiated by the user, please review their account for any suspicious activity.\n\n" +
            "Best regards,\n" +
            "Fleet Sync System",
            userEmail
        );

        sendEmail("mytipstadiwa@gmail.com", subject, body);
    }
    
    public void sendRegistrationEmail(String userEmail, String username, String password, String fullname) {
        String subject = "Welcome to Fleet Sync - Account Created Successfully";

        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to Fleet Sync! Your account has been successfully created. Here are your login details:\n\n" +
            "Username: %s\n" +
            "Password: %s\n\n" +
            "To access your account, visit:\n%s\n\n" +
            "If you need any assistance, feel free to contact our support team.\n\n" +
            "Best regards,\n" +
            "Fleet Sync Support Team\n" +
            "%s",
            fullname, username, password, SUPPORT_LINK, SUPPORT_LINK
        );

        sendEmail(userEmail, subject, body);
    }
}


