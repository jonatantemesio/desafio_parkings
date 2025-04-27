package com.estapar.parking_management.repository;

import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByExitTimeIsNull();

    @Query("SELECT v FROM Vehicle v WHERE v.spot = :spot AND v.exitTime IS NULL")
    Optional<Vehicle> findParkedVehicle(@Param("spot") ParkingSpot spot);
}
