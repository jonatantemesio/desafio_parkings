package com.estapar.parking_management.service;

import com.estapar.parking_management.exceptions.GarageClosedException;
import com.estapar.parking_management.exceptions.InvalidParkingOperationException;
import com.estapar.parking_management.exceptions.SpotNotFoundException;
import com.estapar.parking_management.exceptions.VehicleNotFoundException;
import com.estapar.parking_management.model.dto.GarageConfigDTO;
import com.estapar.parking_management.model.dto.VehicleEventDTO;
import com.estapar.parking_management.model.entity.BillingRecord;
import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.BillingRepository;
import com.estapar.parking_management.repository.ParkingSpotRepository;
import com.estapar.parking_management.repository.SectorRepository;
import com.estapar.parking_management.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GarageService {

    private final SectorRepository sectorRepository;
    private final ParkingSpotRepository spotRepository;
            private final VehicleRepository vehicleRepository;
    private final PricingService pricingService;
    private final BillingRepository billingRepository;

    public GarageConfigDTO getCurrentConfiguration() {
        List<Sector> sectors = sectorRepository.findAll();
        List<ParkingSpot> spots = spotRepository.findAll();

        return GarageConfigDTO.builder()
                .sectors(mapSectorsToDto(sectors))
                .spots(mapSpotsToDto(spots))
                .build();
    }

    public void handleEntry(VehicleEventDTO event) {
        vehicleRepository.findByLicensePlate(event.getLicensePlate())
                .ifPresentOrElse(
                        existingVehicle -> {
                            Optional.ofNullable(existingVehicle.getExitTime())
                                    .orElseThrow(() -> new InvalidParkingOperationException("Veículo não pode entrar sem ter efetuado uma saída"));

                            existingVehicle.setEntryTime(event.getEntryTime());
                            existingVehicle.setParkedTime(null);
                            existingVehicle.setExitTime(null);

                            vehicleRepository.saveAndFlush(existingVehicle);
                        },
                        () -> {
                            Sector sector = findBestAvailableSector(LocalTime.from(event.getEntryTime()))
                                    .orElseThrow(() -> new GarageClosedException("Nenhum setor disponível no momento"));

                            Vehicle newVehicle = Vehicle.builder()
                                    .licensePlate(event.getLicensePlate())
                                    .entryTime(event.getEntryTime())
                                    .sector(sector)
                                    .build();

                            sector.setCurrentOccupancy(sector.getCurrentOccupancy() + 1);
                            updateSectorStatus(sector);

                            vehicleRepository.saveAndFlush(newVehicle);
                        }
                );
    }

    public void handleParked(VehicleEventDTO event) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(event.getLicensePlate())
                .orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getEntryTime() != null && event.getParkedTime().isBefore(vehicle.getEntryTime())) {
            throw new InvalidParkingOperationException("Horário de estacionamento não pode ser anterior à entrada");
        }

        ParkingSpot spot = spotRepository.findByCoordinates(event.getLat(), event.getLng())
                .orElseThrow(SpotNotFoundException::new);

        Optional.ofNullable(spot.getVehicle())
                .ifPresent(v -> { throw new InvalidParkingOperationException("Vaga já ocupada"); });

        vehicle.setParkedTime(event.getParkedTime());
        vehicle.setSpot(spot);
        vehicleRepository.save(vehicle);
    }

    public void handleExit(VehicleEventDTO event) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(event.getLicensePlate())
                .orElseThrow(VehicleNotFoundException::new);

        if (vehicle.getParkedTime() != null && event.getExitTime().isBefore(vehicle.getParkedTime())) {
            throw new InvalidParkingOperationException("Horário de saída não pode ser anterior ao horário de estacionamento");
        }

        vehicle.setExitTime(event.getExitTime());
        vehicle.setCurrentPrice(pricingService.calculateFee(vehicle));

        Optional.ofNullable(vehicle.getSpot())
                .ifPresent(spot -> spot.setVehicle(null));

        Optional.ofNullable(vehicle.getSector()).ifPresent(sector -> {
            sector.setCurrentOccupancy(sector.getCurrentOccupancy() - 1);
            updateSectorStatus(sector);
        });

        BillingRecord billingRecord = BillingRecord.fromVehicle(vehicle, vehicle.getCurrentPrice());
        billingRepository.save(billingRecord);
        vehicleRepository.save(vehicle);
    }

    private Optional<Sector> findBestAvailableSector(LocalTime entryTime) {
        return sectorRepository.findOpenSectorsOrderByOccupancy(entryTime)
                .stream()
                .filter(Sector::getIsOpen)
                .findFirst();
    }

    private void updateSectorStatus(Sector sector) {
        double occupancyRate = (double) sector.getCurrentOccupancy() / sector.getMaxCapacity();
        sector.setIsOpen(occupancyRate < 1.0);
        sectorRepository.save(sector);
    }

    private List<GarageConfigDTO.SectorDTOConfig> mapSectorsToDto(List<Sector> sectors) {
        return sectors.stream()
                .map(sector -> GarageConfigDTO.SectorDTOConfig.builder()
                        .sector(sector.getName())
                        .basePrice(sector.getBasePrice())
                        .maxCapacity(sector.getMaxCapacity())
                        .openHour(formatTime(sector.getOpenHour()))
                        .closeHour(formatTime(sector.getCloseHour()))
                        .durationLimitMinutes(sector.getDurationLimitMinutes())
                        .build())
                .toList();
    }

    private List<GarageConfigDTO.SpotDTOConfig> mapSpotsToDto(List<ParkingSpot> spots) {
        return spots.stream()
                .map(spot -> GarageConfigDTO.SpotDTOConfig.builder()
                        .id(spot.getId())
                        .sector(spot.getSector().getName())
                        .lat(spot.getLatitude())
                        .lng(spot.getLongitude())
                        .build())
                .toList();
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}