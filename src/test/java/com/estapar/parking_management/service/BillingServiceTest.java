package com.estapar.parking_management.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.estapar.parking_management.model.dto.RevenueDTO;
import com.estapar.parking_management.model.entity.BillingRecord;
import com.estapar.parking_management.repository.BillingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRevenue_Success() {
        String requestDate = "2025-04-25";
        String sector = "IT";
        RevenueDTO.RevenueDTORequest request = new RevenueDTO.RevenueDTORequest(requestDate, sector);

        BillingRecord record1 = new BillingRecord();
        record1.setAmount(BigDecimal.valueOf(100));
        BillingRecord record2 = new BillingRecord();
        record2.setAmount(BigDecimal.valueOf(200));
        List<BillingRecord> billingRecords = List.of(record1, record2);

        LocalDateTime start = LocalDate.parse(requestDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(requestDate).plusDays(1).atStartOfDay();

        when(billingRepository.findByDateAndSector(start, end, sector)).thenReturn(billingRecords);
        RevenueDTO.RevenueDTOResponse response = billingService.revenue(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(300), response.getAmount());
        assertEquals("BRL", response.getCurrency());
    }

    @Test
    void testRevenue_InvalidDateFormat() {
        String requestDate = "invalid-date";
        String sector = "IT";
        RevenueDTO.RevenueDTORequest request = new RevenueDTO.RevenueDTORequest(requestDate, sector);
        assertThrows(DateTimeParseException.class, () -> billingService.revenue(request));
    }
}
