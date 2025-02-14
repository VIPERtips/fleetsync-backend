package com.example.fleetsync.service;

import com.example.fleetsync.model.Company;
import com.example.fleetsync.model.Status;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.repository.VehicleRepository;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    public Vehicle registerVehicle(Vehicle vehicle, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String username = jwtService.extractUsername(token);
        User user = userService.getUserByUsername(username);
        
        if (vehicleRepository.existsByVin(vehicle.getVin())) {
            throw new RuntimeException("Vehicle with VIN " + vehicle.getVin() + " already exists.");
        }
        if (user != null && user.getCompany() != null) {
            
            vehicle.setCompany(user.getCompany());
            return vehicleRepository.save(vehicle);
        } else {
            throw new RuntimeException("User does not have an associated company");
        }
    }

    public Vehicle getVehicleById(int vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        return vehicleOpt.orElse(null);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle updateVehicle(int vehicleId, Vehicle vehicleRequest) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            vehicle.setVin(vehicleRequest.getVin());
            vehicle.setMake(vehicleRequest.getMake());
            vehicle.setModel(vehicleRequest.getModel());
            vehicle.setLicencePlate(vehicleRequest.getLicencePlate());
            vehicle.setYear(vehicleRequest.getYear());
            vehicle.setStatus(vehicleRequest.getStatus());
            return vehicleRepository.save(vehicle);
        }
        return null;
    }

    public boolean deleteVehicle(int vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isPresent()) {
            vehicleRepository.delete(vehicleOpt.get());
            return true;
        }
        return false;
    }
    
    public List<Vehicle> getVehiclesByCompany(Company company) {
        return vehicleRepository.findByCompany(company);
    }

    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

	public boolean existsByVin(String vin) {
		return vehicleRepository.existsByVin(vin);
	}
	
	public List<Vehicle> getTotalVehiclesByCompany(Company company) {
	    return vehicleRepository.findByCompany(company); 
	}

	public List<Vehicle> getActiveVehiclesByCompany(Company company) {
	    return vehicleRepository.findByCompanyAndStatus(company, Status.ACTIVE);  
	}

	public List<Vehicle> getInactiveVehiclesByCompany(Company company) {
	    return vehicleRepository.findByCompanyAndStatus(company, Status.INACTIVE);  
	}

	public List<Vehicle> getMaintenanceVehiclesByCompany(Company company) {
	    return vehicleRepository.findByCompanyAndStatus(company, Status.MAINTENANCE);  
	}

}
