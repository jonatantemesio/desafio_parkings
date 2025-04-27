package com.estapar.parking_management.repository;

import com.estapar.parking_management.model.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    Optional<Sector> findByName(String name);

    @Query("SELECT s FROM Sector s WHERE s.isOpen = true " +
            "AND :entryTime BETWEEN s.openHour AND s.closeHour " +
            "AND s.currentOccupancy < s.maxCapacity " +
            "ORDER BY (s.currentOccupancy * 1.0 / s.maxCapacity) ASC")
    List<Sector> findOpenSectorsOrderByOccupancy(@Param("entryTime") LocalTime entryTime);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.sector = :sector AND v.exitTime IS NULL")
    int countCurrentOccupancy(@Param("sector") Sector sector);
}
