package com.example.fleetsync.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.fleetsync.model.PaymentRequest;
import com.example.fleetsync.model.StripeResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentService {

    @Value("${stripe.secretkey}")
    private String secretKey;

    public StripeResponse createCheckoutSession(PaymentRequest req) {
        Stripe.apiKey = secretKey;
        long amount = calculateAmount(req.getNumberOfVehicles());

        StripeResponse response = new StripeResponse();

        if(amount == 0) {
            response.setStatus("FREE");
            response.setMessage("No payment required for up to 2 vehicles.");
            return response;
        }

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency(req.getCurrency() == null ? "USD" : req.getCurrency())
            .setUnitAmount(amount)
            .setProductData(
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Fleet Subscription Payment")
                .build()
            )
            .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(priceData)
            .build();

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("https://fleet-sync.vercel.app/payment-success?session_id={CHECKOUT_SESSION_ID}")
            
            .setCancelUrl("https://fleet-sync.vercel.app/payment-cancel")
            .addLineItem(lineItem)
            .putMetadata("numberOfVehicles", String.valueOf(req.getNumberOfVehicles()))
            .build();

        try {
            Session session = Session.create(params);
            response.setStatus("200 OK");
            response.setMessage("Payment session created");
            response.setSessionId(session.getId());
            response.setSessionUrl(session.getUrl());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Stripe session creation failed");
        }
        return response;
    }

    // Pricing logic:
    // 1 or 2 vehicles -> free
    // 3 to 10 vehicles -> $49 (4900 cents)
    // 11 to 50 vehicles -> $149 (14900 cents)
    // >50 vehicles -> multiply cost by number of blocks of 50 vehicles
    private long calculateAmount(int numberOfVehicles) {
    	 System.err.println("Calculating amount for fleet size: " + numberOfVehicles);
        if(numberOfVehicles <= 2) {
            return 0;
        } else if(numberOfVehicles <= 10) {
            return 4900;
        } else if(numberOfVehicles <= 50) {
            return 14900;
        } else {
            int blocks = (int) Math.ceil(numberOfVehicles / 50.0);
            return blocks * 14900;
        }
    }
}
