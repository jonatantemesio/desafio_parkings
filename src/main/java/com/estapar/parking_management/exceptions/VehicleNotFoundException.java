package com.estapar.parking_management.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException() {
        super("Vehicle not found");
    }
}
