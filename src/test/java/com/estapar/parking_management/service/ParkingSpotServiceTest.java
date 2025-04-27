package com.estapar.parking_management.service;

import com.estapar.parking_management.exceptions.SpotNotFoundException;
import com.estapar.parking_management.model.dto.ParkingSpotDTO;
import com.estapar.parking_management.model.dto.SpotStatusDTO;
import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.ParkingSpotRepository;
import com.estapar.parking_management.repository.SectorRepository;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    @Test
    void testCreateParkingSpot_Success() {
        ParkingSpotDTO parkingSpotDTO = ParkingSpotDTO.builder()
                .latitude(BigDecimal.valueOf(10.0))
                .longitude(BigDecimal.valueOf(20.0))
                .sectorId(1L)
                .build();

        Sector sector = Sector.builder()
                .name("Sector A")
                .maxCapacity(50)
                .currentOccupancy(100)
                .durationLimitMinutes(30)
                .openHour(LocalTime.of(8, 0))
                .closeHour(LocalTime.of(18, 0))
                .build();

        ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1L)
                .sector(sector)
                .build();
        Long expectedId = 1L;

        when(sectorRepository.findById(parkingSpotDTO.getSectorId())).thenReturn(Optional.of(sector));
        when(mapper.map(parkingSpotDTO, ParkingSpot.class)).thenReturn(parkingSpot);
        when(parkingSpotRepository.save(parkingSpot)).thenReturn(parkingSpot);

        Long actualId = parkingSpotService.createParkingSpot(parkingSpotDTO);
        assertThat(actualId).isEqualTo(expectedId);
        verify(sectorRepository).findById(parkingSpotDTO.getSectorId());
        verify(parkingSpotRepository).save(parkingSpot);
    }

    @Test
    void testSpotStatus_Success() {
        SpotStatusDTO.SpotStatusDTORequest request = SpotStatusDTO.SpotStatusDTORequest.builder()
                .lat(BigDecimal.valueOf(10.0))
                .lng(BigDecimal.valueOf(20.0))
                .build();

        Sector sector = Sector.builder()
                .name("Sector A")
                .maxCapacity(50)
                .currentOccupancy(100)
                .durationLimitMinutes(30)
                .openHour(LocalTime.of(8, 0))
                .closeHour(LocalTime.of(18, 0))
                .build();

        ParkingSpot spot = ParkingSpot.builder()
                .id(1L)
                .sector(sector)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .licensePlate("ABC123")
                .entryTime(LocalDateTime.now())
                .parkedTime(null)
                .exitTime(null)
                .build();

        spot.setVehicle(vehicle);

        BigDecimal price = BigDecimal.valueOf(50.0);
        SpotStatusDTO.SpotStatusDTOResponse expectedResponse = SpotStatusDTO.SpotStatusDTOResponse.builder()
                .licensePlate("ABC123")
                .priceUntilNow(price)
                .entryTime(vehicle.getEntryTime())
                .timeParked(vehicle.getParkedTime())
                .occupied(true)
                .build();

        when(parkingSpotRepository.findByCoordinates(request.getLat(), request.getLng())).thenReturn(Optional.of(spot));
        when(pricingService.calculateCurrentPrice(spot.getVehicle())).thenReturn(price);

        SpotStatusDTO.SpotStatusDTOResponse actualResponse = parkingSpotService.spotStatus(request);

        assertThat(actualResponse.getPriceUntilNow()).isEqualTo(expectedResponse.getPriceUntilNow());
        verify(parkingSpotRepository).findByCoordinates(request.getLat(), request.getLng());
        verify(pricingService).calculateCurrentPrice(vehicle);
    }

    @Test
    void testCreateParkingSpot_SectorNotFound() {
        ParkingSpotDTO parkingSpotDTO = ParkingSpotDTO.builder()
                .latitude(BigDecimal.valueOf(10.0))
                .longitude(BigDecimal.valueOf(20.0))
                .sectorId(999L)
                .build();

        when(sectorRepository.findById(parkingSpotDTO.getSectorId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> parkingSpotService.createParkingSpot(parkingSpotDTO)).isInstanceOf(SpotNotFoundException.class);
        verify(sectorRepository).findById(parkingSpotDTO.getSectorId());
    }

    @Test
    void testSpotStatus_SpotNotFound() {
        SpotStatusDTO.SpotStatusDTORequest request = SpotStatusDTO.SpotStatusDTORequest.builder()
                .lat(BigDecimal.valueOf(10.0))
                .lng(BigDecimal.valueOf(20.0))
                .build();

        when(parkingSpotRepository.findByCoordinates(request.getLat(), request.getLng())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> parkingSpotService.spotStatus(request)).isInstanceOf(SpotNotFoundException.class);
        verify(parkingSpotRepository).findByCoordinates(request.getLat(), request.getLng());
    }
}

