package com.estapar.parking_management.service;

import com.estapar.parking_management.model.dto.SectorDTO;
import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.repository.SectorRepository;
import com.github.dozermapper.core.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final Mapper mapper;

    public Long createSector(SectorDTO sectorDTO) {
        Sector sector = sectorRepository.save(mapper.map(sectorDTO, Sector.class));
        return sector.getId();
    }


}
