package com.example.fleetsync.service;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.Subscription;
import com.example.fleetsync.model.User;
import com.example.fleetsync.repository.CompanyRepository;
import com.example.fleetsync.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private CompanyRepository companyRepository;

   
    
    @Autowired
    private UserService userService;
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
    
    public void updateSubscriptionAfterPayment(int companyId, int numberOfVehicles) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company not found.");
        }

        Company company = companyOpt.get();

        Subscription subscription = subscriptionRepository.findTopByCompanyOrderByEndDateDesc(company)
                .orElse(new Subscription());

        subscription.setCompany(company);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1)); 
        subscription.setNumberOfVehiclesAllowed(numberOfVehicles);
        subscription.setPaid(true);

        subscriptionRepository.save(subscription); 
    }
    
    public Subscription getSubscriptionForUser(String username) {
        User user = userService.getUserByUsername(username);  
        Company company = user.getCompany();  
        return company.getSubscription(); 
    }
    public boolean updateSubscription(Subscription subscription, String username) {
        User user = userService.getUserByUsername(username);
        if (user == null || user.getCompany() == null) {
            return false; 
        }

        Company company = user.getCompany();
        Subscription existingSubscription = company.getSubscription();
        if (existingSubscription == null) {
            return false;          }

        existingSubscription.setNumberOfVehiclesAllowed(subscription.getNumberOfVehiclesAllowed());
        subscriptionRepository.save(existingSubscription);
        return true;
    }

}
