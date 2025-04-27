package com.estapar.parking_management.service;

import com.estapar.parking_management.exceptions.VehicleNotFoundException;
import com.estapar.parking_management.model.dto.PlateStatusDTO;
import com.estapar.parking_management.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final PricingService pricingService;

    public PlateStatusDTO.PlateStatusDTOResponse getPlateStatus(PlateStatusDTO.PlateStatusDTORequest request) {
        return vehicleRepository.findByLicensePlate(request.getLicensePlate())
                .map(vehicle -> PlateStatusDTO.PlateStatusDTOResponse.builder()
                        .licensePlate(vehicle.getLicensePlate())
                        .entryTime(vehicle.getEntryTime())
                        .timeParked(vehicle.getParkedTime())
                        .lat(vehicle.getSpot().getLatitude())
                        .lng(vehicle.getSpot().getLongitude())
                        .priceUntilNow(pricingService.calculateCurrentPrice(vehicle))
                        .build())
                .orElseThrow(VehicleNotFoundException::new);
    }

}
