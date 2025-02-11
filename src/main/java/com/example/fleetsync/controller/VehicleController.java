package com.example.fleetsync.controller;

import com.example.fleetsync.model.ApiResponse;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.UserService;
import com.example.fleetsync.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Vehicle>> registerVehicle(@RequestBody Vehicle vehicle, HttpServletRequest request) {
        try {
            
            Vehicle registeredVehicle = vehicleService.registerVehicle(vehicle, request);
            return ResponseEntity.ok(new ApiResponse<>("Vehicle registered successfully", true, registeredVehicle));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        if (vehicles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>("No vehicles found", false, null));
        }
        return ResponseEntity.ok(new ApiResponse<>("Vehicles retrieved successfully", true, vehicles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> getVehicleById(@PathVariable int id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null) {
            return ResponseEntity.ok(new ApiResponse<>("Vehicle retrieved successfully", true, vehicle));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Vehicle not found with ID: " + id, false, null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(@PathVariable int id, @RequestBody Vehicle vehicleRequest) {
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicleRequest);
        if (updatedVehicle != null) {
            return ResponseEntity.ok(new ApiResponse<>("Vehicle updated successfully", true, updatedVehicle));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Vehicle not found with ID: " + id, false, null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<String>> deleteVehicle(@PathVariable int id) {
        boolean isDeleted = vehicleService.deleteVehicle(id);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>("Vehicle deleted successfully", true, null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Vehicle not found with ID: " + id, false, null));
    }
    
    @GetMapping("/my-vehicles")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getUserVehicles(HttpServletRequest request) {
        try {
            
            String token = extractTokenFromRequest(request);

            
            String username = jwtService.extractUsername(token);

            
			User user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("User not found", false, null));
            }

            
            List<Vehicle> vehicles = vehicleService.getVehiclesByCompany(user.getCompany());

            if (vehicles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>("No vehicles found for the user", false, null));
            }

            return ResponseEntity.ok(new ApiResponse<>("User vehicles retrieved successfully", true, vehicles));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), false, null));
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
