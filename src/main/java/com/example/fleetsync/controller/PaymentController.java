package com.example.fleetsync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.PaymentRequest;
import com.example.fleetsync.model.PaymentSuccessRequest;
import com.example.fleetsync.model.StripeResponse;
import com.example.fleetsync.model.User;
import com.example.fleetsync.service.CompanyService;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.StripePaymentService;
import com.example.fleetsync.service.SubscriptionService;
import com.example.fleetsync.service.UserService;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private StripePaymentService stripePaymentService;
    
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
    
    
    @Autowired
    private SubscriptionService subscriptionService;

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
    public ResponseEntity<String> handlePaymentSuccess(@RequestBody PaymentSuccessRequest req, HttpServletRequest request) {
        try {
            
            String token = extractTokenFromRequest(request);

            
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            Company company = companyService.getCompanyByUser(user);
            int companyId = company.getCompanyId(); 
            int numberOfVehicles = company.getVehicles().size();
            Session session = Session.retrieve(req.getSessionId());
            System.out.println("Stripe Session: " + session);
            if ("paid".equals(session.getPaymentStatus())) {
               
                subscriptionService.updateSubscriptionAfterPayment(companyId, numberOfVehicles);
                return ResponseEntity.ok("Payment verified successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment.");
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
