package com.example.fleetsync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.User;

public interface CompanyRepository  extends JpaRepository<Company, Integer> {

	boolean existsByName(String name);

	Optional<Company> findByUser(User user);

}
