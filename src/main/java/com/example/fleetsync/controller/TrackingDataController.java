package com.example.fleetsync.controller;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.model.User;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.repository.VehicleRepository;
import com.example.fleetsync.service.JwtService;
import com.example.fleetsync.service.TrackingDataService;
import com.example.fleetsync.service.UserService;
import com.example.fleetsync.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/tracking")
public class TrackingDataController {

    @Autowired
    private TrackingDataService trackingDataService;
    
    @Autowired 
    private VehicleService vehicleService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final ConcurrentHashMap<String, Boolean> activeTrackingSessions = new ConcurrentHashMap<>();

    
    @PostMapping("/start/{vin}")
    public ResponseEntity<Map<String, String>> startTracking(@PathVariable String vin) {
        Map<String, String> response = new HashMap<>();

        
        if (!vehicleService.existsByVin(vin)) {
            response.put("message", "VIN does not exist in the database: " + vin);
            return ResponseEntity.badRequest().body(response);
        }

        
        if (activeTrackingSessions.containsKey(vin)) {
            response.put("message", "Tracking already started for VIN: " + vin);
        } else {
            activeTrackingSessions.put(vin, true);
            response.put("message", "Tracking started for VIN: " + vin);
        }
        return ResponseEntity.ok(response);
    }


    
    @PostMapping("/stop/{vin}")
    public String stopTracking(@PathVariable String vin) {
        if (!activeTrackingSessions.containsKey(vin)) {
            return "No active tracking session found for VIN: " + vin;
        }
        activeTrackingSessions.remove(vin);
        return "Tracking stopped for VIN: " + vin;
    }

    
    @PostMapping
    public TrackingData trackVehicle(@RequestParam String vin, @RequestParam Double longitude, 
                                     @RequestParam Double latitude, @RequestParam Double speed) {
    	 Vehicle vehicle = vehicleRepository.findByVin(vin)
    	            .orElseThrow(() -> new RuntimeException("Vehicle not found for VIN: " + vin));

    	   
    	    int vehicleId = vehicle.getVehicleId();
        
        TrackingData trackingData = trackingDataService.saveTrackingData(vehicleId, longitude, latitude, speed);

        messagingTemplate.convertAndSend("/topic/vehicle/" + vehicleId, trackingData);

        return trackingData;
    }

    
    @GetMapping("/vehicle/{vehicleId}")
    public List<TrackingData> getTrackingDataByVehicleId(@PathVariable int vehicleId) {
        return trackingDataService.getTrackingDataByVehicleId(vehicleId);
    }

    
    @GetMapping("/vin/{vin}")
    public TrackingData getTrackingData(@PathVariable String vin) {
    	TrackingData trackingData = trackingDataService.getCurrentTrackingData(vin);
    	System.err.println(trackingData);
        return trackingData;
    }

    
    @GetMapping("/history/{vin}")
    public List<TrackingData> getTrackingHistory(@PathVariable String vin) {
    	System.err.println(trackingDataService.getTrackingHistory(vin));
        return trackingDataService.getTrackingHistory(vin);
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Double>> getAnalytics(HttpServletRequest request) {
        try {
            
            String token = extractTokenFromRequest(request);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", 0.0)); // User not found
            }

            
            List<Vehicle> userVehicles = vehicleService.getVehiclesByCompany(user.getCompany());

            if (userVehicles.isEmpty()) {
                return ResponseEntity.ok(Map.of("totalDistance", 0.0, "averageSpeed", 0.0));
            }

            // Extract vehicle IDs
            List<Integer> vehicleIds = userVehicles.stream()
                    .map(Vehicle::getVehicleId)
                    .toList();

           
            List<TrackingData> allTrackingData = trackingDataService.getTrackingDataByVehicleIds(vehicleIds);

            if (allTrackingData.isEmpty()) {
                return ResponseEntity.ok(Map.of("totalDistance", 0.0, "averageSpeed", 0.0));
            }

            // Group data by vehicle ID
            Map<Integer, List<TrackingData>> groupedData = allTrackingData.stream()
                    .collect(Collectors.groupingBy(td -> td.getVehicle().getVehicleId()));

            double totalDistance = 0.0;

            // Calculate total distance
            for (List<TrackingData> trackingList : groupedData.values()) {
                trackingList.sort(Comparator.comparing(TrackingData::getTimestamp));
                for (int i = 1; i < trackingList.size(); i++) {
                    TrackingData prev = trackingList.get(i - 1);
                    TrackingData curr = trackingList.get(i);
                    totalDistance += calculateDistance(
                            prev.getLatitude(), prev.getLongitude(),
                            curr.getLatitude(), curr.getLongitude()
                    );
                }
            }

            // Calculate average speed
            double totalSpeed = allTrackingData.stream()
                    .filter(td -> td.getSpeed() != null)
                    .mapToDouble(TrackingData::getSpeed)
                    .sum();
            long speedCount = allTrackingData.stream()
                    .filter(td -> td.getSpeed() != null)
                    .count();
            double averageSpeed = speedCount > 0 ? totalSpeed / speedCount : 0;

            Map<String, Double> analytics = new HashMap<>();
            analytics.put("totalDistance", totalDistance);
            analytics.put("averageSpeed", averageSpeed);

            return ResponseEntity.ok(analytics);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", 0.0));
        }
    }

    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    
    private String extractTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}


}
