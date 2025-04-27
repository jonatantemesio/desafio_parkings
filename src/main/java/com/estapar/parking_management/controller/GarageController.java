package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.dto.GarageConfigDTO;
import com.estapar.parking_management.model.dto.VehicleEventDTO;
import com.estapar.parking_management.service.GarageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleEvent(@RequestBody VehicleEventDTO event) {
        switch (event.getEventType()) {
            case ENTRY -> garageService.handleEntry(event);
            case PARKED -> garageService.handleParked(event);
            case EXIT -> garageService.handleExit(event);
            default -> throw new IllegalArgumentException("");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/garage")
    public ResponseEntity<GarageConfigDTO> getGarageConfiguration() {
        GarageConfigDTO config = garageService.getCurrentConfiguration();
        return ResponseEntity.ok(config);
    }
}
