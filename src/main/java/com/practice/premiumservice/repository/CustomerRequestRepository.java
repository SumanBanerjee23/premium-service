package com.practice.premiumservice.repository;

import com.practice.premiumservice.entity.CustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {
    
    Optional<CustomerRequest> findByPostalCodeAndVehicleType(String postalCode, String vehicleType);
    
    List<CustomerRequest> findByPostalCode(String postalCode);
    
    List<CustomerRequest> findByVehicleType(String vehicleType);
    
    Page<CustomerRequest> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT cr FROM CustomerRequest cr WHERE cr.state = :state")
    List<CustomerRequest> findByState(@Param("state") String state);
    
    @Query("SELECT COUNT(cr) FROM CustomerRequest cr WHERE cr.postalCode = :postalCode")
    long countByPostalCode(@Param("postalCode") String postalCode);
    
    @Query("SELECT COUNT(cr) FROM CustomerRequest cr WHERE cr.vehicleType = :vehicleType")
    long countByVehicleType(@Param("vehicleType") String vehicleType);
    
    @Query("SELECT cr.vehicleType, COUNT(cr) FROM CustomerRequest cr GROUP BY cr.vehicleType")
    List<Object[]> getVehicleTypeStatistics();
    
    List<CustomerRequest> findByCity(String city);
    
    List<CustomerRequest> findByAnnualMileageBetween(Integer minMileage, Integer maxMileage);
    
    List<CustomerRequest> findByCreatedAtAfter(LocalDateTime dateTime);
}
