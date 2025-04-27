package com.estapar.parking_management.handler;

import com.estapar.parking_management.exceptions.GarageClosedException;
import com.estapar.parking_management.exceptions.InvalidParkingOperationException;
import com.estapar.parking_management.exceptions.SpotNotFoundException;
import com.estapar.parking_management.exceptions.VehicleNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            GarageClosedException.class,
            SpotNotFoundException.class,
            VehicleNotFoundException.class,
            InvalidParkingOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleCustomExceptions(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Getter
    @Setter
    public static class ErrorResponse {
        private String message;
        private LocalDateTime timestamp;
    }
}
