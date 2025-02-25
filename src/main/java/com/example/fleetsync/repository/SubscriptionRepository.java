package com.example.fleetsync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
	Optional<Subscription> findByCompany_CompanyId(int companyId);
	
Optional<Subscription> findByCompanyAndPaidTrue(Company company);
    
    Optional<Subscription> findTopByCompanyOrderByEndDateDesc(Company company);

}
