package com.estapar.parking_management.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RevenueDTO {

    private RevenueDTO() {}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDTORequest {
        @NotNull(message = "Date must not be null")
        @NotBlank(message = "Date must not be blank")
        private String date;

        @NotNull(message = "Sector must not be null")
        @NotBlank(message = "Sector must not be blank")
        private String sector;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueDTOResponse {
        private BigDecimal amount;
        private String currency;
        private LocalDateTime timestamp;
    }

}
