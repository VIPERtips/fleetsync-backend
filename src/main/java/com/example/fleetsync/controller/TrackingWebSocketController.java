package com.example.fleetsync.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.service.TrackingDataService;

@Controller
public class TrackingWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final TrackingDataService trackingDataService; // Ensure this service is handling DB operations

    public TrackingWebSocketController(SimpMessagingTemplate messagingTemplate, TrackingDataService trackingDataService) {
        this.messagingTemplate = messagingTemplate;
        this.trackingDataService = trackingDataService;
    }

    @MessageMapping("/track")  // Android app sends messages here
    public void receiveLocationUpdate(@Payload TrackingData trackingData) {
        // Save to database
        trackingDataService.saveTrackingData(
                trackingData.getVehicle().getVehicleId(),
                trackingData.getLongitude(),
                trackingData.getLatitude(),
                trackingData.getSpeed()
        );

        // Broadcast real-time location updates to subscribed clients
        messagingTemplate.convertAndSend("/topic/vehicle/" + trackingData.getVehicle().getVehicleId(), trackingData);
    }
}

