package com.estapar.parking_management.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpotDTO {

    private Long sectorId;

    @JsonProperty("lat")
    private BigDecimal latitude;

    @JsonProperty("lng")
    private BigDecimal longitude;


}
