package com.example.fleetsync.service;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.Subscription;
import com.example.fleetsync.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

   
    public boolean canAddVehicle(Company company, int numberOfVehiclesToAdd) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByCompany_CompanyId(company.getCompanyId());
        if (subscriptionOpt.isEmpty()) {
            return false;  
        }

        Subscription subscription = subscriptionOpt.get();

        
        return subscription.isValid() && (company.getVehicles().size() + numberOfVehiclesToAdd <= subscription.getNumberOfVehiclesAllowed());
    }

   
    public boolean canTrack(Company company) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByCompany_CompanyId(company.getCompanyId());
        if (subscriptionOpt.isEmpty()) {
            return false;  
        }

        Subscription subscription = subscriptionOpt.get();
        return subscription.isValid() && subscription.isPaid();  
    }
}
