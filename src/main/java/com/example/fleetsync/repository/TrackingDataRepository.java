package com.example.fleetsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fleetsync.model.TrackingData;

public interface TrackingDataRepository extends JpaRepository<TrackingData, Integer> {

	List<TrackingData> findByVehicle_VehicleId(int vehicleId);
	@Query(value = "SELECT t.id AS id, v.vehicle_id AS vehicle_id, v.vin AS vin, " +
            "t.longitude AS longitude, t.latitude AS latitude, " +
            "t.timestamp AS timestamp, t.speed AS speed " +
            "FROM vehicle v " +
            "INNER JOIN tracking_data t ON v.vehicle_id = t.vehicle_id " +
            "WHERE v.vin = :vin " +
            "ORDER BY t.timestamp DESC " +
            "LIMIT 1", nativeQuery = true)
TrackingData findMostRecentTrackingByVin(@Param("vin") String vin);

	//TrackingData findTopByVehicle_VinOrderByTimestampDesc(String vin);

	List<TrackingData> findByVehicle_VinOrderByTimestampDesc(String vin);

}
