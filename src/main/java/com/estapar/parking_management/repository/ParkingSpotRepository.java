package com.estapar.parking_management.repository;

import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    @Query("SELECT ps FROM ParkingSpot ps WHERE " +
            "ABS(ps.latitude - :lat) < 0.0001 AND " +
            "ABS(ps.longitude - :lng) < 0.0001")
    Optional<ParkingSpot> findByCoordinates(@Param("lat") BigDecimal lat,
                                            @Param("lng") BigDecimal lng);

    List<ParkingSpot> findBySectorAndVehicleIsNull(Sector sector);
}
