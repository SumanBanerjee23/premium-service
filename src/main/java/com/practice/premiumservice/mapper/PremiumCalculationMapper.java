package com.practice.premiumservice.mapper;

import com.practice.premiumservice.dto.PremiumCalculationRequest;
import com.practice.premiumservice.dto.PremiumCalculationResponse;
import com.practice.premiumservice.entity.CustomerRequest;
import com.practice.premiumservice.entity.PremiumCalculation;

/**
 * Mapper utility class for converting between PremiumCalculation entities and DTOs.
 * Follows industry standards with static utility methods for object mapping.
 */
public final class PremiumCalculationMapper {

    private PremiumCalculationMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a PremiumCalculation entity to a PremiumCalculationResponse DTO.
     *
     * @param calculation the premium calculation entity
     * @return the corresponding response DTO
     */
    public static PremiumCalculationResponse toResponse(PremiumCalculation calculation) {
        if (calculation == null) {
            return null;
        }

        CustomerRequest customerRequest = calculation.getCustomerRequest();
        
        return new PremiumCalculationResponse(
            calculation.getId(),
            customerRequest != null ? customerRequest.getAnnualMileage() : null,
            customerRequest != null ? customerRequest.getPostalCode() : null,
            customerRequest != null ? customerRequest.getVehicleType() : null,
            customerRequest != null ? customerRequest.getState() : null,
            customerRequest != null ? customerRequest.getCity() : null,
            calculation.getMileageFactor(),
            calculation.getVehicleTypeFactor(),
            calculation.getRegionFactor(),
            calculation.getCalculatedPremium(),
            calculation.getCalculatedAt()
        );
    }

    /**
     * Converts a PremiumCalculationRequest DTO to a CustomerRequest entity.
     *
     * @param request the premium calculation request DTO
     * @return the corresponding CustomerRequest entity
     */
    public static CustomerRequest toCustomerRequest(PremiumCalculationRequest request) {
        if (request == null) {
            return null;
        }

        return new CustomerRequest(
            request.annualMileage(),
            request.postalCode(),
            request.vehicleType()
        );
    }
}
