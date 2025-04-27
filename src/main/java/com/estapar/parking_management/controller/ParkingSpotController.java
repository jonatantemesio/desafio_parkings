package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.dto.ParkingSpotDTO;
import com.estapar.parking_management.model.dto.SpotStatusDTO;
import com.estapar.parking_management.service.ParkingSpotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @PostMapping("/parkingSpot")
    public ResponseEntity<Map<String, Long>> createSector(@RequestBody ParkingSpotDTO parkingSpotDTO) {
        Long sectorId = parkingSpotService.createParkingSpot(parkingSpotDTO);
        return ResponseEntity.ok(Map.of("id", sectorId));
    }

    @PostMapping("/spot-status")
    public ResponseEntity<SpotStatusDTO.SpotStatusDTOResponse> getSpotStatus(@RequestBody SpotStatusDTO.SpotStatusDTORequest request) {
        return ResponseEntity.ok(parkingSpotService.spotStatus(request));
    }

}
