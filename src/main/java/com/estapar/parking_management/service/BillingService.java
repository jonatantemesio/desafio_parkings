package com.estapar.parking_management.service;

import com.estapar.parking_management.model.dto.RevenueDTO;
import com.estapar.parking_management.model.entity.BillingRecord;
import com.estapar.parking_management.repository.BillingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BillingService {

    private final BillingRepository billingRepository;

    public RevenueDTO.RevenueDTOResponse revenue(RevenueDTO.RevenueDTORequest request) {
        return Optional.of(LocalDate.parse(request.getDate()))
                .map(date -> {
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.plusDays(1).atStartOfDay();
                    return billingRepository.findByDateAndSector(start, end, request.getSector());
                })
                .map(records -> records.stream()
                        .map(BillingRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .map(total -> RevenueDTO.RevenueDTOResponse.builder()
                        .amount(total)
                        .currency("BRL")
                        .timestamp(LocalDateTime.now())
                        .build())
                .orElseThrow(() -> new NoSuchElementException("Nenhum dado encontrado para esse setor e data"));
    }
}
