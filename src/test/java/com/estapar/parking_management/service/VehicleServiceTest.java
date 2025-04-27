package com.estapar.parking_management.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.estapar.parking_management.exceptions.VehicleNotFoundException;
import com.estapar.parking_management.model.dto.PlateStatusDTO;
import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private PlateStatusDTO.PlateStatusDTORequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Sector sector = Sector.builder()
                .name("Sector A")
                .maxCapacity(50)
                .currentOccupancy(30)
                .durationLimitMinutes(100)
                .openHour(LocalTime.of(8, 0))
                .closeHour(LocalTime.of(18, 0))
                .build();

        ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1L)
                .sector(sector)
                .build();

        vehicle = Vehicle.builder()
                .licensePlate("ABC123")
                .entryTime(LocalDateTime.now().minusMinutes(30))
                .parkedTime(LocalDateTime.now())
                .spot(parkingSpot)
                .sector(sector)
                .build();

        request = new PlateStatusDTO.PlateStatusDTORequest("ABC123");
    }

    @Test
    void testGetPlateStatus_Success() {
        when(vehicleRepository.findByLicensePlate(request.getLicensePlate())).thenReturn(Optional.of(vehicle));
        when(pricingService.calculateCurrentPrice(vehicle)).thenReturn(BigDecimal.valueOf(30.00));

        PlateStatusDTO.PlateStatusDTOResponse response = vehicleService.getPlateStatus(request);

        assertNotNull(response);
        assertEquals("ABC123", response.getLicensePlate());
        assertNotNull(response.getEntryTime());
        assertNotNull(response.getTimeParked());
        assertEquals(30.00, response.getPriceUntilNow().doubleValue(), 0.01);
    }

    @Test
    void testGetPlateStatus_VehicleNotFound() {
        when(vehicleRepository.findByLicensePlate(request.getLicensePlate())).thenReturn(Optional.empty());
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getPlateStatus(request));
    }
}


