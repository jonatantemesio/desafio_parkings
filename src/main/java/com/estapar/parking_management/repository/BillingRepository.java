package com.estapar.parking_management.repository;

import com.estapar.parking_management.model.entity.BillingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillingRepository extends JpaRepository<BillingRecord, Long> {
    @Query("""
    SELECT br FROM BillingRecord br
    WHERE br.exitTime >= :start
      AND br.exitTime < :end
      AND br.sector.name = :sector
    """)
    List<BillingRecord> findByDateAndSector(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("sector") String sector);
}
