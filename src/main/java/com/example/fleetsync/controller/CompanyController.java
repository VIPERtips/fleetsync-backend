package com.example.fleetsync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.fleetsync.model.ApiResponse;
import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.User;
import com.example.fleetsync.service.CompanyService;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
        try {
            List<Company> companies = companyService.getAllCompanies();
            return ResponseEntity.ok(new ApiResponse<>("Companies retrieved successfully", true, companies));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>("No companies found", false, null));
        }
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Company>> getCompanyById(@PathVariable int id) {
        try {
            Company company = companyService.getCompanyById(id);
            return ResponseEntity.ok(new ApiResponse<>("Company retrieved successfully", true, company));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Company not found with ID: " + id, false, null));
        }
    }
 
    @GetMapping("/my-company")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> getCompanyByUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);

            Company company = companyService.getCompanyByUser(user);
            return ResponseEntity.ok(new ApiResponse<>("Company retrieved successfully", true, company));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("No company found for the user", false, null));
        }
    }


    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Company>> registerCompany(HttpServletRequest request, @RequestBody Company companyRequest) {
        try {
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);

            Company createdCompany = companyService.registerCompany(companyRequest, user.getUserId());
            return ResponseEntity.ok(new ApiResponse<>("Company registered successfully", true, createdCompany));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }

    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable int id, @RequestBody Company companyRequest, HttpServletRequest request) {
        try {
            
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);

           
            User user = userService.getUserByUsername(username);

            
            Company existingCompany = companyService.getCompanyById(id);

            
            if (existingCompany.getUser().getUserId() != user.getUserId()) {
                throw new RuntimeException("You can only update the company you own.");
            }

            
            Company updatedCompany = companyService.updateCompany(id, companyRequest);
            return ResponseEntity.ok(new ApiResponse<>("Company updated successfully", true, updatedCompany));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }


    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCompany(@PathVariable int id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.ok(new ApiResponse<>("Company deleted successfully.", true, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Company not found with ID: " + id, false, null));
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
