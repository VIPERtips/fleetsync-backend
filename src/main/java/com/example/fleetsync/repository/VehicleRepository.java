package com.example.fleetsync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{

	boolean existsByVin(String vin);

	List<Vehicle> findByCompany(Company company);

	Optional<Vehicle> findByVin(String vin);

}
