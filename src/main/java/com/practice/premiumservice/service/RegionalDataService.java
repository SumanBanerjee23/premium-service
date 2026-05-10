package com.practice.premiumservice.service;

import com.practice.premiumservice.model.PostalCodeData;
import com.practice.premiumservice.util.PostalCodeDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegionalDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegionalDataService.class);
    
    private final PostalCodeDataLoader postalCodeDataLoader;
    
    // German federal state region factors (configurable)
    private final Map<String, BigDecimal> regionFactors = new ConcurrentHashMap<>();
    
    // Vehicle type factors (configurable)
    private final Map<String, BigDecimal> vehicleTypeFactors = new ConcurrentHashMap<>();
    
    public RegionalDataService(PostalCodeDataLoader postalCodeDataLoader) {
        this.postalCodeDataLoader = postalCodeDataLoader;
        initializeRegionFactors();
        initializeVehicleTypeFactors();
    }
    
    private void initializeRegionFactors() {
        // German federal state factors - these can be made configurable
        regionFactors.put("Baden-Württemberg", new BigDecimal("1.2"));
        regionFactors.put("Bayern", new BigDecimal("1.1"));
        regionFactors.put("Berlin", new BigDecimal("1.3"));
        regionFactors.put("Brandenburg", new BigDecimal("0.9"));
        regionFactors.put("Bremen", new BigDecimal("1.1"));
        regionFactors.put("Hamburg", new BigDecimal("1.3"));
        regionFactors.put("Hessen", new BigDecimal("1.1"));
        regionFactors.put("Mecklenburg-Vorpommern", new BigDecimal("0.8"));
        regionFactors.put("Niedersachsen", new BigDecimal("1.0"));
        regionFactors.put("Nordrhein-Westfalen", new BigDecimal("1.2"));
        regionFactors.put("Rheinland-Pfalz", new BigDecimal("1.0"));
        regionFactors.put("Saarland", new BigDecimal("0.9"));
        regionFactors.put("Sachsen", new BigDecimal("0.8"));
        regionFactors.put("Sachsen-Anhalt", new BigDecimal("0.8"));
        regionFactors.put("Schleswig-Holstein", new BigDecimal("1.0"));
        regionFactors.put("Thüringen", new BigDecimal("0.8"));
        
        logger.info("Initialized {} region factors", regionFactors.size());
    }
    
    private void initializeVehicleTypeFactors() {
        // Vehicle type factors - these can be made configurable
        vehicleTypeFactors.put("CAR", new BigDecimal("1.0"));
        vehicleTypeFactors.put("MOTORCYCLE", new BigDecimal("0.7"));
        vehicleTypeFactors.put("TRUCK", new BigDecimal("1.5"));
        vehicleTypeFactors.put("VAN", new BigDecimal("1.2"));
        vehicleTypeFactors.put("ELECTRIC_CAR", new BigDecimal("0.8"));
        vehicleTypeFactors.put("HYBRID_CAR", new BigDecimal("0.9"));
        vehicleTypeFactors.put("LUXURY_CAR", new BigDecimal("1.8"));
        vehicleTypeFactors.put("SPORTS_CAR", new BigDecimal("1.6"));
        vehicleTypeFactors.put("SUV", new BigDecimal("1.4"));
        
        logger.info("Initialized {} vehicle type factors", vehicleTypeFactors.size());
    }
    
    public BigDecimal getRegionFactor(String postalCode) {
        PostalCodeData postalCodeData = getPostalCodeData(postalCode);
        if (postalCodeData == null) {
            logger.warn("Postal code not found: {}, using default region factor", postalCode);
            return BigDecimal.ONE; // Default factor
        }
        
        String state = postalCodeData.getState();
        BigDecimal factor = regionFactors.get(state);
        
        if (factor == null) {
            logger.warn("Region factor not found for state: {}, using default factor", state);
            return BigDecimal.ONE; // Default factor
        }
        
        return factor;
    }
    
    public BigDecimal getVehicleTypeFactor(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle type cannot be null or empty");
        }
        
        String normalizedVehicleType = vehicleType.toUpperCase().trim();
        BigDecimal factor = vehicleTypeFactors.get(normalizedVehicleType);
        
        if (factor == null) {
            logger.warn("Vehicle type factor not found for: {}, using default factor", normalizedVehicleType);
            return BigDecimal.ONE; // Default factor for unknown vehicle types
        }
        
        return factor;
    }
    
    public PostalCodeData getPostalCodeData(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return null;
        }
        
        return postalCodeDataLoader.findByPostalCode(postalCode.trim());
    }
    
    public boolean isPostalCodeValid(String postalCode) {
        return postalCodeDataLoader.isPostalCodeValid(postalCode);
    }
    
    public Map<String, BigDecimal> getAllRegionFactors() {
        return new ConcurrentHashMap<>(regionFactors);
    }
    
    public Map<String, BigDecimal> getAllVehicleTypeFactors() {
        return new ConcurrentHashMap<>(vehicleTypeFactors);
    }
    
    public void updateRegionFactor(String state, BigDecimal factor) {
        if (state == null || state.trim().isEmpty() || factor == null || factor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid state or factor");
        }
        regionFactors.put(state.trim(), factor);
        logger.info("Updated region factor for {}: {}", state, factor);
    }
    
    public void updateVehicleTypeFactor(String vehicleType, BigDecimal factor) {
        if (vehicleType == null || vehicleType.trim().isEmpty() || factor == null || factor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid vehicle type or factor");
        }
        vehicleTypeFactors.put(vehicleType.toUpperCase().trim(), factor);
        logger.info("Updated vehicle type factor for {}: {}", vehicleType, factor);
    }
}
