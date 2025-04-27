package com.estapar.parking_management.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 10)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column(nullable = false)
    private LocalDateTime exitTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @PrePersist
    public void prePersist() {
        if (currency == null) {
            currency = "BRL";
        }
    }

    public static BillingRecord fromVehicle(Vehicle vehicle, BigDecimal amount) {
        return BillingRecord.builder()
                .licensePlate(vehicle.getLicensePlate())
                .sector(vehicle.getSector())
                .entryTime(vehicle.getEntryTime())
                .exitTime(vehicle.getExitTime())
                .amount(amount)
                .build();
    }
}
