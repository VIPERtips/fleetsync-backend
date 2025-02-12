package com.example.fleetsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.TrackingData;

public interface TrackingDataRepository extends JpaRepository<TrackingData, Integer> {

	List<TrackingData> findByVehicle_VehicleId(int vehicleId);

	TrackingData findTopByVehicle_VinOrderByTimestampDesc(String vin);

	List<TrackingData> findByVehicle_VinOrderByTimestampAsc(String vin);

}
