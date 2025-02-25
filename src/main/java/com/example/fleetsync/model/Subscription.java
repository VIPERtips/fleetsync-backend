package com.example.fleetsync.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subscriptionId;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "companyId", nullable = false)
    private Company company;

    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfVehiclesAllowed;
    private boolean paid;

    public Subscription() {
        // Default constructor
    }

    public Subscription(Company company, LocalDate startDate, LocalDate endDate, int numberOfVehiclesAllowed, boolean paid) {
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfVehiclesAllowed = numberOfVehiclesAllowed;
        this.paid = paid;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfVehiclesAllowed() {
        return numberOfVehiclesAllowed;
    }

    /*public void setNumberOfVehiclesAllowed(int numberOfVehiclesAllowed) {
        this.numberOfVehiclesAllowed = numberOfVehiclesAllowed;
    }*/
    
    public void setNumberOfVehiclesAllowed(int numberOfVehiclesAllowed) {
        if (numberOfVehiclesAllowed < company.getFleetSize()) {
            throw new IllegalArgumentException(
                "Subscription limit must be ≥ current fleet size (" + 
                company.getFleetSize() + ")"
            );
        }
        this.numberOfVehiclesAllowed = numberOfVehiclesAllowed;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

   
    public boolean isValid() {
        return paid && LocalDate.now().isBefore(endDate);
    }
}
