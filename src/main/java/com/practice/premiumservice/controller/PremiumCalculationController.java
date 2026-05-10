package com.practice.premiumservice.controller;

import com.practice.premiumservice.dto.PremiumCalculationRequest;
import com.practice.premiumservice.dto.PremiumCalculationResponse;
import com.practice.premiumservice.entity.CustomerRequest;
import com.practice.premiumservice.entity.PremiumCalculation;
import com.practice.premiumservice.mapper.PremiumCalculationMapper;
import com.practice.premiumservice.service.PremiumCalculationService;
import com.practice.premiumservice.service.RegionalDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/premium")
@Tag(name = "Premium Calculation", description = "API for calculating insurance premiums")
public class PremiumCalculationController {
    
    private static final Logger logger = LoggerFactory.getLogger(PremiumCalculationController.class);
    
    private final PremiumCalculationService premiumCalculationService;
    private final RegionalDataService regionalDataService;
    
    public PremiumCalculationController(PremiumCalculationService premiumCalculationService,
                                      RegionalDataService regionalDataService) {
        this.premiumCalculationService = premiumCalculationService;
        this.regionalDataService = regionalDataService;
    }
    
    @PostMapping("/calculate")
    @Operation(
        summary = "Calculate insurance premium",
        description = "Calculates insurance premium based on mileage, vehicle type, and postal code"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Premium calculated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PremiumCalculationResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "id": 1,
                      "annualMileage": 15000,
                      "postalCode": "79189",
                      "vehicleType": "CAR",
                      "state": "Baden-Württemberg",
                      "city": "Bad Krozingen",
                      "mileageFactor": 1.5,
                      "vehicleTypeFactor": 1.0,
                      "regionFactor": 1.2,
                      "calculatedPremium": 1.80,
                      "calculatedAt": "2024-01-15T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Postal code not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PremiumCalculationResponse> calculatePremium(
            @Valid @RequestBody PremiumCalculationRequest request) {
        
        logger.info("Received premium calculation request: {}", request);
        
        // Validate postal code
        if (!regionalDataService.isPostalCodeValid(request.postalCode())) {
            logger.warn("Invalid postal code: {}", request.postalCode());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        CustomerRequest customerRequest = PremiumCalculationMapper.toCustomerRequest(request);
        
        PremiumCalculation calculation = premiumCalculationService.calculatePremium(customerRequest);
        PremiumCalculationResponse response = PremiumCalculationMapper.toResponse(calculation);
        
        logger.info("Premium calculation completed for id - {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/calculations/{id}")
    @Operation(
        summary = "Get premium calculation by ID",
        description = "Retrieves a specific premium calculation by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculation found"),
        @ApiResponse(responseCode = "404", description = "Calculation not found")
    })
    public ResponseEntity<PremiumCalculationResponse> getCalculationById(
            @Parameter(description = "Calculation ID", required = true)
            @PathVariable Long id) {
        
        PremiumCalculation calculation = premiumCalculationService.getCalculationById(id);
        PremiumCalculationResponse response = PremiumCalculationMapper.toResponse(calculation);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/vehicle-types")
    @Operation(
        summary = "Get available vehicle types",
        description = "Returns list of supported vehicle types and their factors"
    )
    @ApiResponse(responseCode = "200", description = "Vehicle types retrieved successfully")
    public ResponseEntity<Object> getVehicleTypes() {
        return ResponseEntity.ok(regionalDataService.getAllVehicleTypeFactors());
    }
    
    @GetMapping("/region-factors")
    @Operation(
        summary = "Get region factors",
        description = "Returns region factors for all German federal states"
    )
    @ApiResponse(responseCode = "200", description = "Region factors retrieved successfully")
    public ResponseEntity<Object> getRegionFactors() {
        return ResponseEntity.ok(regionalDataService.getAllRegionFactors());
    }
    
    @GetMapping("/statistics/average-premium/{vehicleType}")
    @Operation(
        summary = "Get average premium by vehicle type",
        description = "Calculates average premium for a specific vehicle type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average premium calculated"),
        @ApiResponse(responseCode = "404", description = "No calculations found for vehicle type")
    })
    public ResponseEntity<Object> getAveragePremiumByVehicleType(
            @Parameter(description = "Vehicle type", required = true)
            @PathVariable String vehicleType) {
        
        var averagePremium = premiumCalculationService.getAveragePremiumByVehicleType(vehicleType);
        if (averagePremium == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(new AveragePremiumResponse(vehicleType, averagePremium));
    }
    
    private record AveragePremiumResponse(String vehicleType, java.math.BigDecimal averagePremium) {}
}
