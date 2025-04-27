package com.estapar.parking_management.service;

import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PricingServiceTest {

    @Mock
    private Vehicle vehicle;

    @Mock
    private Sector sector;

    @InjectMocks
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateCurrentPrice() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime parkedTime = LocalDateTime.now();
        when(vehicle.getEntryTime()).thenReturn(entryTime);
        when(vehicle.getParkedTime()).thenReturn(parkedTime);
        when(vehicle.getSector()).thenReturn(sector);
        when(sector.getBasePrice()).thenReturn(BigDecimal.valueOf(60));
        when(sector.getCurrentOccupancy()).thenReturn(50);
        when(sector.getMaxCapacity()).thenReturn(100);

        BigDecimal price = pricingService.calculateCurrentPrice(vehicle);
        assertNotNull(price);
        assertEquals(0, price.compareTo(BigDecimal.valueOf(30.00)));
    }

    @Test
    void testCalculateFee() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime exitTime = LocalDateTime.now();
        when(vehicle.getEntryTime()).thenReturn(entryTime);
        when(vehicle.getExitTime()).thenReturn(exitTime);
        when(vehicle.getSector()).thenReturn(sector);
        when(sector.getBasePrice()).thenReturn(BigDecimal.valueOf(60));
        BigDecimal fee = pricingService.calculateFee(vehicle);
        assertNotNull(fee);
        assertEquals(0, fee.compareTo(BigDecimal.valueOf(75.00)));
    }

    @Test
    void testCalculatePriceWithDynamicPricing() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime exitTime = LocalDateTime.now();
        when(vehicle.getEntryTime()).thenReturn(entryTime);
        when(vehicle.getExitTime()).thenReturn(exitTime);
        when(vehicle.getSector()).thenReturn(sector);
        when(sector.getBasePrice()).thenReturn(BigDecimal.valueOf(60));
        when(sector.getCurrentOccupancy()).thenReturn(30);
        when(sector.getMaxCapacity()).thenReturn(50);

        BigDecimal fee = pricingService.calculateFee(vehicle);

        assertNotNull(fee);
        assertTrue(fee.compareTo(BigDecimal.valueOf(60)) > 0);
    }

    @Test
    void testValidateVehicleTimes() {
        Sector sector = Sector.builder()
                .basePrice(BigDecimal.valueOf(60))
                .currentOccupancy(5)
                .maxCapacity(100)
                .build();

        when(vehicle.getEntryTime()).thenReturn(LocalDateTime.now().minusMinutes(30));
        when(vehicle.getSector()).thenReturn(sector);

        assertDoesNotThrow(() -> pricingService.calculateCurrentPrice(vehicle));
    }

    @Test
    void testValidateVehicleTimesThrowsException() {
        when(vehicle.getEntryTime()).thenReturn(null);
        when(vehicle.getSector()).thenReturn(sector);
        assertThrows(NullPointerException.class, () -> pricingService.calculateCurrentPrice(vehicle));
    }
}

