package com.practice.premiumservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for premium calculation")
public record PremiumCalculationRequest(
    
    @Schema(description = "Annual mileage in kilometers", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Annual mileage is required")
    @Min(value = 0, message = "Annual mileage cannot be negative")
    @Max(value = 1000000, message = "Annual mileage exceeds reasonable limit")
    Integer annualMileage,
    
    @Schema(description = "Postal code of vehicle registration", example = "79189", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Postal code is required")
    String postalCode,
    
    @Schema(description = "Vehicle type", example = "CAR", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
        "CAR", "MOTORCYCLE", "TRUCK", "VAN", "ELECTRIC_CAR", "HYBRID_CAR", "LUXURY_CAR", "SPORTS_CAR", "SUV"
    })
    @NotBlank(message = "Vehicle type is required")
    String vehicleType
) {
}
