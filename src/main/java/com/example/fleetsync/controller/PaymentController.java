package com.example.fleetsync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fleetsync.model.PaymentRequest;
import com.example.fleetsync.model.PaymentSuccessRequest;
import com.example.fleetsync.model.StripeResponse;
import com.example.fleetsync.service.StripePaymentService;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private StripePaymentService stripePaymentService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody PaymentRequest req) {
        StripeResponse stripeResponse = stripePaymentService.createCheckoutSession(req);
       try {
    	   return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
	} catch (Exception e) {
		 return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
    }
    
    @PostMapping("/payment/success")
    public ResponseEntity<String> handlePaymentSuccess(@RequestBody PaymentSuccessRequest req) {
        try {
            // Validate sessionId with Stripe API
            Session session = Session.retrieve(req.getSessionId());
            if ("completed".equals(session.getPaymentStatus())) {
                // Mark the payment as successful in your system
                return ResponseEntity.ok("Payment verified successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment.");
        }
    }

}
