package com.practice.premiumservice.repository;

import com.practice.premiumservice.entity.PremiumCalculation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumCalculationRepository extends JpaRepository<PremiumCalculation, Long> {
    
    Page<PremiumCalculation> findByCalculatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT pc FROM PremiumCalculation pc WHERE pc.calculatedPremium BETWEEN :min AND :max")
    List<PremiumCalculation> findByPremiumRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    @Query("SELECT AVG(pc.calculatedPremium) FROM PremiumCalculation pc WHERE pc.customerRequest.vehicleType = :vehicleType")
    BigDecimal getAveragePremiumByVehicleType(@Param("vehicleType") String vehicleType);
    
    @Query("SELECT COUNT(pc) FROM PremiumCalculation pc WHERE pc.calculatedPremium > :threshold")
    long countPremiumsAboveThreshold(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT SUM(pc.calculatedPremium) FROM PremiumCalculation pc WHERE pc.calculatedAt >= :since")
    BigDecimal getTotalPremiumSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT pc.customerRequest.state, COUNT(pc), AVG(pc.calculatedPremium) " +
           "FROM PremiumCalculation pc " +
           "GROUP BY pc.customerRequest.state")
    List<Object[]> getPremiumStatisticsByState();
}
