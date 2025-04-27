package com.estapar.parking_management.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "sector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private LocalTime openHour;

    @Column(nullable = false)
    private LocalTime closeHour;

    @Column(nullable = false)
    private Integer durationLimitMinutes;

    @Column(nullable = false)
    private Integer currentOccupancy = 0;

    @Column(nullable = false)
    private Boolean isOpen = true;

    @OneToMany(mappedBy = "sector")
    private List<ParkingSpot> spots;

    @OneToMany(mappedBy = "sector")
    private List<Vehicle> vehicles;
}
