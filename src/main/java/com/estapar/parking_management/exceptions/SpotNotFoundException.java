package com.estapar.parking_management.exceptions;

public class SpotNotFoundException extends RuntimeException {
    public SpotNotFoundException() {
        super("Parking spot not found");
    }
}
