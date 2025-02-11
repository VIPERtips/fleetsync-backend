package com.example.fleetsync.controller;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.service.TrackingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingDataController {

    @Autowired
    private TrackingDataService trackingDataService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    
    @PostMapping
    public TrackingData trackVehicle(@RequestParam int vehicleId, @RequestParam Double longitude, 
                                     @RequestParam Double latitude, @RequestParam Double speed) {
        
        TrackingData trackingData = trackingDataService.saveTrackingData(vehicleId, longitude, latitude, speed);

        
        messagingTemplate.convertAndSend("/topic/vehicle/" + vehicleId, trackingData);

        return trackingData;
    }

    
    @GetMapping("/{vehicleId}")
    public List<TrackingData> getTrackingDataByVehicleId(@PathVariable int vehicleId) {
        return trackingDataService.getTrackingDataByVehicleId(vehicleId);
    }
}
