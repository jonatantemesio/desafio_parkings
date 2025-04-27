package com.estapar.parking_management.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEventDTO {

    @JsonProperty("license_plate")
    private String licensePlate;

    @JsonProperty("entry_time")
    private LocalDateTime entryTime;

    @JsonProperty("exit_time")
    private LocalDateTime exitTime;

    @JsonProperty("parked_time")
    private LocalDateTime parkedTime;

    private BigDecimal lat;

    private BigDecimal lng;

    @JsonProperty("event_type")
    private EventType eventType;

    public enum EventType {
        ENTRY, PARKED, EXIT
    }
}
