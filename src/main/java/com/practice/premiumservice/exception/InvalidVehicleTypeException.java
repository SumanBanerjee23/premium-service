package com.practice.premiumservice.exception;

public class InvalidVehicleTypeException extends RuntimeException {
    
    public InvalidVehicleTypeException(String message) {
        super(message);
    }
    
    public InvalidVehicleTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
