package com.example.fleetsync.service;

import com.example.fleetsync.model.TrackingData;
import com.example.fleetsync.model.Vehicle;
import com.example.fleetsync.repository.TrackingDataRepository;
import com.example.fleetsync.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    
    public TrackingData getCurrentTrackingData(String vin) {
        return trackingDataRepository.findMostRecentTrackingByVin(vin);
    }

    
    public List<TrackingData> getTrackingHistory(String vin) {
        return trackingDataRepository.findByVehicle_VinOrderByTimestampDesc(vin);
    }


	public TrackingData saveTrackingData(Double longitude, Double latitude) {
		TrackingData trackingData = new TrackingData();
        //trackingData.setVehicle(vehicle);  
       // trackingData.setLongitude(intitude);
        trackingData.setLatitude(latitude);
        //trackingData.setSpeed(speed);
        trackingData.setTimestamp(LocalDateTime.now());
        return trackingDataRepository.save(trackingData);	
        }
	
	@Scheduled(cron = "0 */1 * * * ?") // Runs every hour 
    public void cleanUpTrackingData() {
        List<Vehicle> vehicles = vehicleRepository.findAll(); 

        for (Vehicle vehicle : vehicles) {
            List<TrackingData> trackingDataList = trackingDataRepository.findByVehicle_VehicleId(vehicle.getVehicleId());
            
           
            if (trackingDataList.size() > 20) {
               
                trackingDataList.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));

                
                int excessRecords = trackingDataList.size() - 20;
                for (int i = 0; i < excessRecords; i++) {
                    TrackingData dataToDelete = trackingDataList.get(i);
                    trackingDataRepository.delete(dataToDelete);
                    System.err.println("deleted ******************************************************************************************************");
                }
            }
        }
    }
}
