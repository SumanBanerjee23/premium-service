package com.practice.premiumservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "premium_calculations", indexes = {
    @Index(name = "idx_customer_request", columnList = "customerRequestId"),
    @Index(name = "idx_calculated_at", columnList = "calculatedAt")
})
public class PremiumCalculation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_request_id", nullable = false, unique = true)
    private CustomerRequest customerRequest;
    
    @NotNull(message = "Mileage factor is required")
    @DecimalMin(value = "0.0", message = "Mileage factor must be positive")
    @Column(name = "mileage_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal mileageFactor;
    
    @NotNull(message = "Vehicle type factor is required")
    @DecimalMin(value = "0.0", message = "Vehicle type factor must be positive")
    @Column(name = "vehicle_type_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal vehicleTypeFactor;
    
    @NotNull(message = "Region factor is required")
    @DecimalMin(value = "0.0", message = "Region factor must be positive")
    @Column(name = "region_factor", nullable = false, precision = 5, scale = 2)
    private BigDecimal regionFactor;
    
    @NotNull(message = "Calculated premium is required")
    @DecimalMin(value = "0.0", message = "Calculated premium must be positive")
    @Column(name = "calculated_premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal calculatedPremium;
    
    @CreationTimestamp
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;
    
    public PremiumCalculation() {}
    
    public PremiumCalculation(CustomerRequest customerRequest, BigDecimal mileageFactor, 
                             BigDecimal vehicleTypeFactor, BigDecimal regionFactor, 
                             BigDecimal calculatedPremium) {
        this.customerRequest = customerRequest;
        this.mileageFactor = mileageFactor;
        this.vehicleTypeFactor = vehicleTypeFactor;
        this.regionFactor = regionFactor;
        this.calculatedPremium = calculatedPremium;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CustomerRequest getCustomerRequest() {
        return customerRequest;
    }
    
    public void setCustomerRequest(CustomerRequest customerRequest) {
        this.customerRequest = customerRequest;
    }
    
    public BigDecimal getMileageFactor() {
        return mileageFactor;
    }
    
    public void setMileageFactor(BigDecimal mileageFactor) {
        this.mileageFactor = mileageFactor;
    }
    
    public BigDecimal getVehicleTypeFactor() {
        return vehicleTypeFactor;
    }
    
    public void setVehicleTypeFactor(BigDecimal vehicleTypeFactor) {
        this.vehicleTypeFactor = vehicleTypeFactor;
    }
    
    public BigDecimal getRegionFactor() {
        return regionFactor;
    }
    
    public void setRegionFactor(BigDecimal regionFactor) {
        this.regionFactor = regionFactor;
    }
    
    public BigDecimal getCalculatedPremium() {
        return calculatedPremium;
    }
    
    public void setCalculatedPremium(BigDecimal calculatedPremium) {
        this.calculatedPremium = calculatedPremium;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
    
}
