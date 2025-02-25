package com.example.fleetsync.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleetsync.model.ApiResponse;
import com.example.fleetsync.model.Subscription;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.SubscriptionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private JwtService jwtService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-subscription")
    public ResponseEntity<ApiResponse<?>> getMySubscription(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);

            Subscription subscription = subscriptionService.getSubscriptionForUser(username);

            if (subscription != null) {
                return ResponseEntity.ok(new ApiResponse<>("Subscription retrieved successfully", true, subscription));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<String> updateSubscription(@RequestBody Subscription subscription, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);

            boolean isUpdated = subscriptionService.updateSubscription(subscription, username);

            if (isUpdated) {
                return ResponseEntity.ok("Subscription updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating subscription");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating subscription");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
