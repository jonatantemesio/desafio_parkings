package com.estapar.parking_management.exceptions;

public class InvalidParkingOperationException extends RuntimeException {
    public InvalidParkingOperationException(String message) {
        super(message);
    }
}
