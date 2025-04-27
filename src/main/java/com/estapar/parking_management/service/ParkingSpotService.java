package com.estapar.parking_management.service;

import com.estapar.parking_management.exceptions.SpotNotFoundException;
import com.estapar.parking_management.model.dto.ParkingSpotDTO;
import com.estapar.parking_management.model.dto.SpotStatusDTO;
import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.ParkingSpotRepository;
import com.estapar.parking_management.repository.SectorRepository;
import com.github.dozermapper.core.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final SectorRepository sectorRepository;
    private final Mapper mapper;
    private final PricingService pricingService;

    public Long createParkingSpot(ParkingSpotDTO parkingSpotDTO) {
        return sectorRepository.findById(parkingSpotDTO.getSectorId())
                .map(sector -> {
                    ParkingSpot parkingSpot = mapper.map(parkingSpotDTO, ParkingSpot.class);
                    parkingSpot.setSector(sector);
                    return parkingSpotRepository.save(parkingSpot).getId();
                })
                .orElseThrow(SpotNotFoundException::new);
    }

    public SpotStatusDTO.SpotStatusDTOResponse spotStatus(SpotStatusDTO.SpotStatusDTORequest request) {
        ParkingSpot spot = parkingSpotRepository.findByCoordinates(request.getLat(), request.getLng())
                .orElseThrow(SpotNotFoundException::new);

        Vehicle vehicle = Optional.ofNullable(spot.getVehicle()).orElse(new Vehicle());

        BigDecimal price = Optional.ofNullable(spot.getVehicle())
                .map(pricingService::calculateCurrentPrice)
                .orElse(null);

        return SpotStatusDTO.SpotStatusDTOResponse.builder()
                .licensePlate(vehicle.getLicensePlate())
                .priceUntilNow(price)
                .entryTime(vehicle.getEntryTime())
                .timeParked(vehicle.getParkedTime())
                .occupied(spot.getVehicle() != null)
                .build();
    }

}
