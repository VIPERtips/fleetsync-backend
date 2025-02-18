package com.example.fleetsync.model;

public class PaymentRequest {
    private int numberOfVehicles;
    private String currency;

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }
    public void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
