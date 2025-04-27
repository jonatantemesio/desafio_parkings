package com.estapar.parking_management.controller;

import com.estapar.parking_management.model.dto.RevenueDTO;
import com.estapar.parking_management.service.BillingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/revenue")
    public ResponseEntity<RevenueDTO.RevenueDTOResponse> getRevenue(@RequestBody RevenueDTO.RevenueDTORequest request) {
        return ResponseEntity.ok(billingService.revenue(request));

    }

}
