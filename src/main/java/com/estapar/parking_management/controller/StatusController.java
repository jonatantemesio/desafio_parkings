package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.dto.PlateStatusDTO;
import com.estapar.parking_management.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class StatusController {

    private final VehicleService vehicleService;

    @PostMapping("/plate-status")
    public ResponseEntity<PlateStatusDTO.PlateStatusDTOResponse> getPlateStatus(@RequestBody PlateStatusDTO.PlateStatusDTORequest request) {
        return ResponseEntity.ok(vehicleService.getPlateStatus(request));
    }

}
