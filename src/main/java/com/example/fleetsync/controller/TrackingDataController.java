package com.example.fleetsync.controller;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.repository.VehicleRepository;
import com.example.fleetsync.service.TrackingDataService;
import com.example.fleetsync.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@RestController
@RequestMapping("/api/tracking")
public class TrackingDataController {

    @Autowired
    private TrackingDataService trackingDataService;
    
    @Autowired 
    private VehicleService vehicleService;
    
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
}
