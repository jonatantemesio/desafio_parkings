package com.estapar.parking_management.service;

import com.estapar.parking_management.model.entity.Sector;
import com.estapar.parking_management.model.entity.Vehicle;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class PricingService {
    private static final NavigableMap<Double, Double> PRICING_RULES = new TreeMap<>();

    static {
        PRICING_RULES.put(0.25, 0.9);   // < 25% → 10% discount
        PRICING_RULES.put(0.5, 1.0);    // < 50% → no change
        PRICING_RULES.put(0.75, 1.1);   // < 75% → 10% increase
        PRICING_RULES.put(1.0, 1.25);   // ≤ 100% → 25% increase
    }

    public BigDecimal calculateCurrentPrice(Vehicle vehicle) {
        validateVehicleTimes(vehicle);

        LocalDateTime endTime = vehicle.getParkedTime() != null ?
                vehicle.getParkedTime() :
                LocalDateTime.now();

        long minutesParked = Duration.between(vehicle.getEntryTime(), endTime).toMinutes();
        return calculatePrice(vehicle, minutesParked);
    }

    public BigDecimal calculateFee(Vehicle vehicle) {
        validateVehicleTimes(vehicle);
        Objects.requireNonNull(vehicle.getExitTime(), "O horário de saída do veículo não deve ser nulo");

        long minutesParked = Duration.between(vehicle.getEntryTime(), vehicle.getExitTime()).toMinutes();
        return calculatePrice(vehicle, minutesParked);
    }

    private BigDecimal calculatePrice(Vehicle vehicle, long minutesParked) {
        Sector sector = vehicle.getSector();
        BigDecimal basePricePerMinute = sector.getBasePrice()
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        BigDecimal fee = basePricePerMinute.multiply(BigDecimal.valueOf(minutesParked));
        double occupancyRate = calculateOccupancyRate(sector);

        return applyDynamicPricing(fee, occupancyRate);
    }

    private double calculateOccupancyRate(Sector sector) {
        return (double) sector.getCurrentOccupancy() / sector.getMaxCapacity();
    }

    private void validateVehicleTimes(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "O veículo não deve ser nulo");
        Objects.requireNonNull(vehicle.getEntryTime(), "O tempo de entrada do veículo não deve ser nulo");
        Objects.requireNonNull(vehicle.getSector(), "O setor de veículos não deve ser nulo");
    }

    private BigDecimal applyDynamicPricing(BigDecimal fee, double occupancyRate) {
        Map.Entry<Double, Double> entry = PRICING_RULES.ceilingEntry(occupancyRate);
        if (entry == null) {
            entry = PRICING_RULES.lastEntry();
        }
        double multiplier = entry.getValue();
        return fee.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP);
    }
}
