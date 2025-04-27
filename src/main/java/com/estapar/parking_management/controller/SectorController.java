package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.dto.SectorDTO;
import com.estapar.parking_management.service.SectorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sector")
@AllArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> createSector(@RequestBody SectorDTO sectorDTO) {
        Long sectorId = sectorService.createSector(sectorDTO);
        return ResponseEntity.ok(Map.of("id", sectorId));
    }

}
