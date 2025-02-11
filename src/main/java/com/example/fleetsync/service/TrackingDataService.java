package com.example.fleetsync.service;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.repository.TrackingDataRepository;
import com.example.fleetsync.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrackingDataService {

    @Autowired
    private TrackingDataRepository trackingDataRepository;

    @Autowired
    private VehicleRepository vehicleRepository;  
    public TrackingData saveTrackingData(int vehicleId, Double intitude, Double latitude, Double speed) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle not found"));

        TrackingData trackingData = new TrackingData();
        trackingData.setVehicle(vehicle);  
        trackingData.setLongitude(intitude);
        trackingData.setLatitude(latitude);
        trackingData.setSpeed(speed);
        trackingData.setTimestamp(LocalDateTime.now());
        return trackingDataRepository.save(trackingData);
    }

    
    public List<TrackingData> getTrackingDataByVehicleId(int vehicleId) {
        return trackingDataRepository.findByVehicle_VehicleId(vehicleId);
    }
}
