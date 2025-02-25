package com.example.fleetsync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.User;
import com.example.fleetsync.repository.CompanyRepository;
import com.example.fleetsync.repository.UserRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Company> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            throw new RuntimeException("No companies found");
        }
        return companies;
    }

    public Company registerCompany(Company req, int userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));
        
        if (user.getCompany() != null) {
            throw new RuntimeException("User already has a company. A user can only have one company.");
        }

        if (companyRepository.existsByName(req.getName())) {
            throw new RuntimeException("Company name: " + req.getName() + " is already taken.");
        }
        if (req.getFleetSize() <= 0) {
            throw new RuntimeException("Fleet size must be greater than 0.");
        }

        req.setUser(user);
        
        if (req.getLatitude() == 0.0 || req.getLongitude() == 0.0) {
            throw new RuntimeException("Company location (latitude and longitude) is required.");
        }
        Company savedCompany = companyRepository.save(req);

        user.setCompany(savedCompany);
        userRepository.save(user);

        return savedCompany;
    }


    public Company updateCompany(int companyId, Company req) {
        Company existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company with ID " + companyId + " not found."));

        
        if (!existingCompany.getName().equals(req.getName()) && companyRepository.existsByName(req.getName())) {
            throw new RuntimeException("Company name: " + req.getName() + " is already taken. Please use a different name.");
        }
        
        if (req.getFleetSize() <= 0) {
            throw new RuntimeException("Fleet size must be greater than 0.");
        }

       
        existingCompany.setName(req.getName());
        existingCompany.setFleetSize(req.getFleetSize());
        existingCompany.setAddress(req.getAddress());
        existingCompany.setCity(req.getCity());
        existingCompany.setCountry(req.getCountry());
        existingCompany.setLatitude(req.getLatitude());
        existingCompany.setLongitude(req.getLongitude());

        return companyRepository.save(existingCompany);
    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found."));
    }

    public void deleteCompany(int companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company with ID " + companyId + " not found."));

        
        User user = company.getUser();
        if (user != null) {
            user.setCompany(null);
            userRepository.save(user);
        }

        companyRepository.deleteById(companyId);
    }
    
    public Company getCompanyByUser(User user) {
        return companyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No company found for this user."));
    }

}
