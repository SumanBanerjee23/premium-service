package com.practice.premiumservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response containing premium calculation details")
public record PremiumCalculationResponse(
    
    @Schema(description = "Unique identifier of the calculation")
    Long id,
    
    @Schema(description = "Annual mileage used for calculation")
    Integer annualMileage,
    
    @Schema(description = "Postal code used for calculation")
    String postalCode,
    
    @Schema(description = "Vehicle type used for calculation")
    String vehicleType,
    
    @Schema(description = "State derived from postal code")
    String state,
    
    @Schema(description = "City derived from postal code")
    String city,
    
    @Schema(description = "Mileage factor applied")
    BigDecimal mileageFactor,
    
    @Schema(description = "Vehicle type factor applied")
    BigDecimal vehicleTypeFactor,
    
    @Schema(description = "Region factor applied")
    BigDecimal regionFactor,
    
    @Schema(description = "Final calculated premium amount")
    BigDecimal calculatedPremium,
    
    @Schema(description = "Timestamp when calculation was performed")
    LocalDateTime calculatedAt
) {
}
