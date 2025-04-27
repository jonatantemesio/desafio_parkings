package com.estapar.parking_management.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectorDTO {
    private String name;

    private BigDecimal basePrice;

    @JsonProperty("max_capacity")
    private Integer maxCapacity;

    @JsonProperty("open_hour")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openHour;

    @JsonProperty("close_hour")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeHour;

    @JsonProperty("duration_limit_minutes")
    private Integer durationLimitMinutes;

    private Integer currentOccupancy = 0;

    private Boolean isOpen = true;
}
