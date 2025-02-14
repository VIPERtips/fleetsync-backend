package com.example.fleetsync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleetsync.model.ApiResponse;
import com.example.fleetsync.model.AuthenticationResponse;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.UserDto;
import com.example.fleetsync.service.AuthenticationService;
import com.example.fleetsync.service.EmailSender;
import com.example.fleetsync.service.UserService;


@RestController
@RequestMapping("api/auth/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailSender emailSender;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody UserDto req) {
        try {
            AuthenticationResponse response = authenticationService.registerUser(req);
            return ResponseEntity.ok(new ApiResponse<>("Registration successful", true, response.getToken()));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(ex.getMessage(), false, null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody User req) {
        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(req);
            ApiResponse<AuthenticationResponse> response = new ApiResponse<>(
                "Login successful",
                true,
                authResponse
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<AuthenticationResponse> errorResponse = new ApiResponse<>(
                "Login failed: " + e.getMessage(),
                false,
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@RequestParam String refreshToken) {
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>("Token refreshed successfully", true, response));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with this email.");
        }

        userService.generateResetToken(user);

        String resetLink = "https://fleet-sync.vercel.app/reset-password?token=" + user.getResetToken();

        // Notify admin
        emailSender.sendEmail("mytipstadiwa@gmail.com", "Password Reset Requested",
                "A password reset has been requested for the user with email: " + email);

       
        emailSender.sendEmail(user.getUserinfo().getEmail(), "Password Reset Link",
                "Dear " + user.getUsername() + ",\n\nTo reset your password, click the link below:\n\n" + resetLink);
        System.err.println(resetLink);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User user = userService.findByResetToken(token);
        if (user == null || !user.isResetTokenValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        userService.updatePassword(user, newPassword);
        return ResponseEntity.ok("Password reset successfully. You can now log in with your new password.");
    }

       	
}

