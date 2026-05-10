package com.practice.premiumservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_requests", indexes = {
    @Index(name = "idx_postal_code", columnList = "postalCode"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class CustomerRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Annual mileage is required")
    @Min(value = 0, message = "Annual mileage cannot be negative")
    @Max(value = 1000000, message = "Annual mileage exceeds reasonable limit")
    @Column(name = "annual_mileage", nullable = false)
    private Integer annualMileage;
    
    @NotBlank(message = "Postal code is required")
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;
    
    @NotBlank(message = "Vehicle type is required")
    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;
    
    @Column(name = "state", length = 100)
    private String state;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "customerRequest", cascade = CascadeType.ALL)
    private PremiumCalculation premiumCalculation;
    
    public CustomerRequest() {}
    
    public CustomerRequest(Integer annualMileage, String postalCode, String vehicleType) {
        this.annualMileage = annualMileage;
        this.postalCode = postalCode;
        this.vehicleType = vehicleType;
    }
    
    public Long getId() {
        return id;
    }
    
    public Integer getAnnualMileage() {
        return annualMileage;
    }
    
    public void setAnnualMileage(Integer annualMileage) {
        this.annualMileage = annualMileage;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public PremiumCalculation getPremiumCalculation() {
        return premiumCalculation;
    }
    
    public void setPremiumCalculation(PremiumCalculation premiumCalculation) {
        this.premiumCalculation = premiumCalculation;
    }
    
}
