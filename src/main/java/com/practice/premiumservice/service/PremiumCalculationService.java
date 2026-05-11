package com.practice.premiumservice.service;

import com.practice.premiumservice.entity.CustomerRequest;
import com.practice.premiumservice.entity.PremiumCalculation;
import com.practice.premiumservice.exception.ResourceNotFoundException;
import com.practice.premiumservice.repository.CustomerRequestRepository;
import com.practice.premiumservice.repository.PremiumCalculationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class PremiumCalculationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PremiumCalculationService.class);
    
    private final CustomerRequestRepository customerRequestRepository;
    private final PremiumCalculationRepository premiumCalculationRepository;
    private final RegionalDataService regionalDataService;
    
    private static final BigDecimal MILEAGE_FACTOR_0_5000 = new BigDecimal("0.5");
    private static final BigDecimal MILEAGE_FACTOR_5001_10000 = new BigDecimal("1.0");
    private static final BigDecimal MILEAGE_FACTOR_10001_20000 = new BigDecimal("1.5");
    private static final BigDecimal MILEAGE_FACTOR_20000_PLUS = new BigDecimal("2.0");
    
    public PremiumCalculationService(CustomerRequestRepository customerRequestRepository,
                                   PremiumCalculationRepository premiumCalculationRepository,
                                   RegionalDataService regionalDataService) {
        this.customerRequestRepository = customerRequestRepository;
        this.premiumCalculationRepository = premiumCalculationRepository;
        this.regionalDataService = regionalDataService;
    }
    
    @Transactional
    public PremiumCalculation calculatePremium(CustomerRequest customerRequest) {
        logger.info("Calculating premium for postal code: {}, mileage: {}, vehicle type: {}", 
                   customerRequest.getPostalCode(), customerRequest.getAnnualMileage(), customerRequest.getVehicleType());
        
        BigDecimal mileageFactor = calculateMileageFactor(customerRequest.getAnnualMileage());
        BigDecimal vehicleTypeFactor = regionalDataService.getVehicleTypeFactor(customerRequest.getVehicleType());
        BigDecimal regionFactor = regionalDataService.getRegionFactor(customerRequest.getPostalCode());
        
        BigDecimal calculatedPremium = mileageFactor.multiply(vehicleTypeFactor).multiply(regionFactor)
            .setScale(2, RoundingMode.HALF_UP);
        
        PremiumCalculation premiumCalculation = new PremiumCalculation(
            customerRequest, mileageFactor, vehicleTypeFactor, regionFactor, calculatedPremium
        );
        
        // Enrich customer request with regional data
        enrichCustomerRequestWithRegionalData(customerRequest);
        
        // Save entities
        customerRequestRepository.save(customerRequest);
        PremiumCalculation savedCalculation = premiumCalculationRepository.save(premiumCalculation);
        
        logger.info("Premium calculated successfully: {} for request ID: {}", 
                   calculatedPremium, customerRequest.getId());
        
        return savedCalculation;
    }
    
    private BigDecimal calculateMileageFactor(Integer annualMileage) {
        if (annualMileage == null || annualMileage < 0) {
            throw new IllegalArgumentException("Annual mileage must be non-negative");
        }
        
        if (annualMileage <= 5000) {
            return MILEAGE_FACTOR_0_5000;
        } else if (annualMileage <= 10000) {
            return MILEAGE_FACTOR_5001_10000;
        } else if (annualMileage <= 20000) {
            return MILEAGE_FACTOR_10001_20000;
        } else {
            return MILEAGE_FACTOR_20000_PLUS;
        }
    }
    
    private void enrichCustomerRequestWithRegionalData(CustomerRequest customerRequest) {
        var postalCodeData = regionalDataService.getPostalCodeData(customerRequest.getPostalCode());
        if (postalCodeData != null) {
            customerRequest.setState(postalCodeData.getState());
            customerRequest.setCity(postalCodeData.getCity());
        }
    }
    
    @Transactional(readOnly = true)
    public PremiumCalculation getCalculationById(Long id) {
        return premiumCalculationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Premium calculation not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAveragePremiumByVehicleType(String vehicleType) {
        return premiumCalculationRepository.getAveragePremiumByVehicleType(vehicleType);
    }
}
