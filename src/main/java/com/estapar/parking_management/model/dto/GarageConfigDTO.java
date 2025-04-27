package com.estapar.parking_management.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarageConfigDTO {
    @JsonProperty("garage")
    private List<SectorDTOConfig> sectors;
    private List<SpotDTOConfig> spots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SectorDTOConfig {
        private String sector;

        private BigDecimal basePrice;

        @JsonProperty("max_capacity")
        private Integer maxCapacity;

        @JsonProperty("open_hour")
        private String openHour;

        @JsonProperty("close_hour")
        private String closeHour;

        @JsonProperty("duration_limit_minutes")
        private Integer durationLimitMinutes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpotDTOConfig {
        private Long id;
        private String sector;
        private BigDecimal lat;
        private BigDecimal lng;
    }
}
