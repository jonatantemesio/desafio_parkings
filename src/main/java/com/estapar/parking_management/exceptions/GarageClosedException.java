package com.estapar.parking_management.exceptions;

public class GarageClosedException extends RuntimeException {
    public GarageClosedException(String message) {
        super(message);
    }
}
