package com.estapar.parking_management.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SpotStatusDTO {

    private SpotStatusDTO() {}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpotStatusDTORequest {
        private BigDecimal lat;
        private BigDecimal lng;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpotStatusDTOResponse {
        private boolean occupied;

        @JsonProperty("license_plate")
        private String licensePlate;

        @JsonProperty("price_until_now")
        private BigDecimal priceUntilNow;

        @JsonProperty("entry_time")
        private LocalDateTime entryTime;

        @JsonProperty("time_parked")
        private LocalDateTime timeParked;
    }

}
