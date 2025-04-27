package com.estapar.parking_management.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.estapar.parking_management.exceptions.GarageClosedException;
import com.estapar.parking_management.exceptions.InvalidParkingOperationException;
import com.estapar.parking_management.model.dto.GarageConfigDTO;
import com.estapar.parking_management.model.dto.VehicleEventDTO;
import com.estapar.parking_management.model.entity.ParkingSpot;
import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import com.estapar.parking_management.repository.BillingRepository;
import com.estapar.parking_management.repository.ParkingSpotRepository;
import com.estapar.parking_management.repository.SectorRepository;
import com.estapar.parking_management.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

class GaragemServiceTest {

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PricingService pricingService;

    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private GarageService garageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrentConfiguration() {
        List<Sector> sectors = List.of(
                Sector.builder()
                        .name("Sector A")
                        .maxCapacity(50)
                        .currentOccupancy(100)
                        .durationLimitMinutes(30)
                        .openHour(LocalTime.of(8, 0))
                        .closeHour(LocalTime.of(18, 0))
                        .build(),

                Sector.builder()
                        .name("Sector B")
                        .maxCapacity(30)
                        .currentOccupancy(60)
                        .durationLimitMinutes(20)
                        .openHour(LocalTime.of(9, 0))
                        .closeHour(LocalTime.of(17, 0))
                        .build()
        );

        List<ParkingSpot> spots = List.of(
                ParkingSpot.builder()
                        .id(1L)
                        .sector(sectors.get(0))
                        .build(),

                ParkingSpot.builder()
                        .id(2L)
                        .sector(sectors.get(1))
                        .build()
        );

        when(sectorRepository.findAll()).thenReturn(sectors);
        when(spotRepository.findAll()).thenReturn(spots);
        GarageConfigDTO config = garageService.getCurrentConfiguration();
        assertNotNull(config);
        assertEquals(2, config.getSectors().size());
        assertEquals(2, config.getSpots().size());
        assertEquals("Sector A", config.getSectors().get(0).getSector());
    }

    @Test
    void testHandleEntry_ExistingVehicle() {
        String licensePlate = "ABC123";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(LocalDateTime.now())
                .build();

        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setLicensePlate(licensePlate);
        existingVehicle.setExitTime(LocalDateTime.now().minusHours(1));

        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(existingVehicle));
        garageService.handleEntry(event);
        assertEquals(existingVehicle.getEntryTime(), event.getEntryTime());
        verify(vehicleRepository).saveAndFlush(existingVehicle);
    }

    @Test
    void testHandleEntry_NewVehicle_NoSectorAvailable() {
        String licensePlate = "XYZ456";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(LocalDateTime.now())
                .build();

        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.empty());
        when(sectorRepository.findOpenSectorsOrderByOccupancy(LocalTime.from(event.getEntryTime()))).thenReturn(List.of());
        assertThrows(GarageClosedException.class, () -> garageService.handleEntry(event));
    }

    @Test
    void testHandleParked_Success() {
        String licensePlate = "DEF789";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(LocalDateTime.now())
                .parkedTime(LocalDateTime.now().plusHours(1))
                .lat(BigDecimal.valueOf(10.0))
                .lng(BigDecimal.valueOf(20.0))
                .build();

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setEntryTime(LocalDateTime.now());
        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicle));

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

        when(spotRepository.findByCoordinates(event.getLat(), event.getLng())).thenReturn(Optional.of(spot));
        garageService.handleParked(event);
        assertEquals(spot, vehicle.getSpot());
        assertNotNull(vehicle.getParkedTime());
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void testHandleParked_SpotOccupied() {
        String licensePlate = "GHI012";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(LocalDateTime.now())
                .parkedTime(LocalDateTime.now().plusHours(1))
                .lat(BigDecimal.valueOf(10.0))
                .lng(BigDecimal.valueOf(20.0))
                .build();

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setEntryTime(LocalDateTime.now());
        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicle));

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

        spot.setVehicle(new Vehicle());
        when(spotRepository.findByCoordinates(event.getLat(), event.getLng())).thenReturn(Optional.of(spot));
        assertThrows(InvalidParkingOperationException.class, () -> garageService.handleParked(event));
    }

    @Test
    void testHandleExit_Success() {
        String licensePlate = "JKL345";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(LocalDateTime.now())
                .parkedTime(null)
                .exitTime(null)
                .lat(null)
                .lng(null)
                .build();

        event.setExitTime(LocalDateTime.now().plusHours(1));

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setParkedTime(LocalDateTime.now().minusHours(1));
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

        vehicle.setSpot(spot);
        vehicle.setSector(sector);
        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicle));
        when(pricingService.calculateFee(vehicle)).thenReturn(BigDecimal.valueOf(50));
        garageService.handleExit(event);
        assertNotNull(vehicle.getExitTime());
        verify(billingRepository).save(any());
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void testHandleExit_InvalidExitTime() {
        String licensePlate = "MNO678";
        VehicleEventDTO event = VehicleEventDTO.builder()
                .licensePlate(licensePlate)
                .entryTime(null)
                .parkedTime(null)
                .exitTime(null)
                .lat(null)
                .lng(null)
                .build();
        event.setExitTime(LocalDateTime.now().minusHours(1));

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setParkedTime(LocalDateTime.now());
        when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicle));
        assertThrows(InvalidParkingOperationException.class, () -> garageService.handleExit(event));
    }
}

