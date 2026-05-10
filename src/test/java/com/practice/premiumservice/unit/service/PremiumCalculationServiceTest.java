package com.practice.premiumservice.unit.service;

import com.practice.premiumservice.entity.CustomerRequest;
import com.practice.premiumservice.entity.PremiumCalculation;
import com.practice.premiumservice.model.PostalCodeData;
import com.practice.premiumservice.repository.CustomerRequestRepository;
import com.practice.premiumservice.repository.PremiumCalculationRepository;
import com.practice.premiumservice.service.PremiumCalculationService;
import com.practice.premiumservice.service.RegionalDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumCalculationServiceTest {

    @Mock
    private CustomerRequestRepository customerRequestRepository;

    @Mock
    private PremiumCalculationRepository premiumCalculationRepository;

    @Mock
    private RegionalDataService regionalDataService;

    @InjectMocks
    private PremiumCalculationService service;

    @Test
    @DisplayName("should calculate premium successfully for low mileage")
    void shouldCalculatePremiumSuccessfullyForLowMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("79189");
        customerRequest.setAnnualMileage(3000);
        customerRequest.setVehicleType("CAR");

        PremiumCalculation savedCalculation = new PremiumCalculation(
            customerRequest, 
            new BigDecimal("0.5"), 
            new BigDecimal("1.0"), 
            new BigDecimal("1.2"), 
            new BigDecimal("0.60")
        );
        savedCalculation.setId(1L);

        when(regionalDataService.getVehicleTypeFactor("CAR")).thenReturn(new BigDecimal("1.0"));
        when(regionalDataService.getRegionFactor("79189")).thenReturn(new BigDecimal("1.2"));
        when(regionalDataService.getPostalCodeData("79189")).thenReturn(null);
        when(customerRequestRepository.save(any(CustomerRequest.class))).thenReturn(customerRequest);
        when(premiumCalculationRepository.save(any(PremiumCalculation.class))).thenReturn(savedCalculation);

        // Act
        PremiumCalculation result = service.calculatePremium(customerRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCalculatedPremium()).isEqualByComparingTo("0.60");
        assertThat(result.getMileageFactor()).isEqualByComparingTo("0.5");
        assertThat(result.getVehicleTypeFactor()).isEqualByComparingTo("1.0");
        assertThat(result.getRegionFactor()).isEqualByComparingTo("1.2");

        verify(regionalDataService).getVehicleTypeFactor("CAR");
        verify(regionalDataService).getRegionFactor("79189");
        verify(regionalDataService).getPostalCodeData("79189");
        verify(customerRequestRepository).save(customerRequest);
        verify(premiumCalculationRepository).save(any(PremiumCalculation.class));
    }

    @Test
    @DisplayName("should calculate premium successfully for medium mileage")
    void shouldCalculatePremiumSuccessfullyForMediumMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("10115");
        customerRequest.setAnnualMileage(7500);
        customerRequest.setVehicleType("MOTORCYCLE");

        PremiumCalculation savedCalculation = new PremiumCalculation(
            customerRequest, 
            new BigDecimal("1.0"), 
            new BigDecimal("0.7"), 
            new BigDecimal("1.3"), 
            new BigDecimal("0.91")
        );
        savedCalculation.setId(2L);

        when(regionalDataService.getVehicleTypeFactor("MOTORCYCLE")).thenReturn(new BigDecimal("0.7"));
        when(regionalDataService.getRegionFactor("10115")).thenReturn(new BigDecimal("1.3"));
        when(regionalDataService.getPostalCodeData("10115")).thenReturn(null);
        when(customerRequestRepository.save(any(CustomerRequest.class))).thenReturn(customerRequest);
        when(premiumCalculationRepository.save(any(PremiumCalculation.class))).thenReturn(savedCalculation);

        // Act
        PremiumCalculation result = service.calculatePremium(customerRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCalculatedPremium()).isEqualByComparingTo("0.91");
        assertThat(result.getMileageFactor()).isEqualByComparingTo("1.0");
    }

    @Test
    @DisplayName("should calculate premium successfully for high mileage")
    void shouldCalculatePremiumSuccessfullyForHighMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("20095");
        customerRequest.setAnnualMileage(15000);
        customerRequest.setVehicleType("TRUCK");

        PremiumCalculation savedCalculation = new PremiumCalculation(
            customerRequest, 
            new BigDecimal("1.5"), 
            new BigDecimal("1.5"), 
            new BigDecimal("1.0"), 
            new BigDecimal("2.25")
        );
        savedCalculation.setId(3L);

        when(regionalDataService.getVehicleTypeFactor("TRUCK")).thenReturn(new BigDecimal("1.5"));
        when(regionalDataService.getRegionFactor("20095")).thenReturn(new BigDecimal("1.0"));
        when(regionalDataService.getPostalCodeData("20095")).thenReturn(null);
        when(customerRequestRepository.save(any(CustomerRequest.class))).thenReturn(customerRequest);
        when(premiumCalculationRepository.save(any(PremiumCalculation.class))).thenReturn(savedCalculation);

        // Act
        PremiumCalculation result = service.calculatePremium(customerRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCalculatedPremium()).isEqualByComparingTo("2.25");
        assertThat(result.getMileageFactor()).isEqualByComparingTo("1.5");
    }

    @Test
    @DisplayName("should calculate premium successfully for very high mileage")
    void shouldCalculatePremiumSuccessfullyForVeryHighMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("80331");
        customerRequest.setAnnualMileage(25000);
        customerRequest.setVehicleType("SUV");

        PremiumCalculation savedCalculation = new PremiumCalculation(
            customerRequest, 
            new BigDecimal("2.0"), 
            new BigDecimal("1.4"), 
            new BigDecimal("1.1"), 
            new BigDecimal("3.08")
        );
        savedCalculation.setId(4L);

        when(regionalDataService.getVehicleTypeFactor("SUV")).thenReturn(new BigDecimal("1.4"));
        when(regionalDataService.getRegionFactor("80331")).thenReturn(new BigDecimal("1.1"));
        when(regionalDataService.getPostalCodeData("80331")).thenReturn(null);
        when(customerRequestRepository.save(any(CustomerRequest.class))).thenReturn(customerRequest);
        when(premiumCalculationRepository.save(any(PremiumCalculation.class))).thenReturn(savedCalculation);

        // Act
        PremiumCalculation result = service.calculatePremium(customerRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCalculatedPremium()).isEqualByComparingTo("3.08");
        assertThat(result.getMileageFactor()).isEqualByComparingTo("2.0");
    }

    @Test
    @DisplayName("should enrich customer request with regional data")
    void shouldEnrichCustomerRequestWithRegionalData() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("79189");
        customerRequest.setAnnualMileage(5000);
        customerRequest.setVehicleType("CAR");

        var postalCodeData = new PostalCodeData(
            "DE",           // iso3166Alpha2
            "BW",           // iso3166Alpha2RegionCode
            "Baden-Württemberg", // region1 (State)
            null,           // region2
            "Deutschland",  // region3 (Country)
            "Bad Krozingen", // region4 (City/Town)
            "79189",        // postleitzahl (ZIP code)
            "Bad Krozingen", // ort (Location)
            null,           // area1
            null,           // area2
            null,           // latitude
            null,           // longitude
            null,           // zeitzone
            null,           // utc
            null,           // sommerzeit
            "1"             // active
        );

        PremiumCalculation savedCalculation = new PremiumCalculation(
            customerRequest, 
            new BigDecimal("0.5"), 
            new BigDecimal("1.0"), 
            new BigDecimal("1.2"), 
            new BigDecimal("0.60")
        );
        savedCalculation.setId(5L);

        when(regionalDataService.getVehicleTypeFactor("CAR")).thenReturn(new BigDecimal("1.0"));
        when(regionalDataService.getRegionFactor("79189")).thenReturn(new BigDecimal("1.2"));
        when(regionalDataService.getPostalCodeData("79189")).thenReturn(postalCodeData);
        when(customerRequestRepository.save(any(CustomerRequest.class))).thenAnswer(invocation -> {
            CustomerRequest saved = invocation.getArgument(0);
            assertThat(saved.getCity()).isEqualTo("Bad Krozingen");
            assertThat(saved.getState()).isEqualTo("Baden-Württemberg");
            return saved;
        });
        when(premiumCalculationRepository.save(any(PremiumCalculation.class))).thenReturn(savedCalculation);

        // Act
        PremiumCalculation result = service.calculatePremium(customerRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(customerRequest.getCity()).isEqualTo("Bad Krozingen");
        assertThat(customerRequest.getState()).isEqualTo("Baden-Württemberg");
        
        verify(regionalDataService).getPostalCodeData("79189");
        verify(customerRequestRepository).save(customerRequest);
    }

    @Test
    @DisplayName("should reject negative annual mileage")
    void shouldRejectNegativeAnnualMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("79189");
        customerRequest.setAnnualMileage(-1000);
        customerRequest.setVehicleType("CAR");

        // Act + Assert
        assertThatThrownBy(() -> service.calculatePremium(customerRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Annual mileage must be non-negative");

        verifyNoInteractions(regionalDataService);
        verifyNoInteractions(customerRequestRepository);
        verifyNoInteractions(premiumCalculationRepository);
    }

    @Test
    @DisplayName("should reject null annual mileage")
    void shouldRejectNullAnnualMileage() {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setPostalCode("79189");
        customerRequest.setAnnualMileage(null);
        customerRequest.setVehicleType("CAR");

        // Act + Assert
        assertThatThrownBy(() -> service.calculatePremium(customerRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Annual mileage must be non-negative");

        verifyNoInteractions(regionalDataService);
        verifyNoInteractions(customerRequestRepository);
        verifyNoInteractions(premiumCalculationRepository);
    }

    @Test
    @DisplayName("should get calculation by id successfully")
    void shouldGetCalculationByIdSuccessfully() {
        // Arrange
        Long calculationId = 1L;
        PremiumCalculation calculation = new PremiumCalculation();
        calculation.setId(calculationId);
        calculation.setCalculatedPremium(new BigDecimal("1.50"));

        when(premiumCalculationRepository.findById(calculationId)).thenReturn(Optional.of(calculation));

        // Act
        PremiumCalculation result = service.getCalculationById(calculationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(calculationId);
        assertThat(result.getCalculatedPremium()).isEqualByComparingTo("1.50");

        verify(premiumCalculationRepository).findById(calculationId);
    }

    @Test
    @DisplayName("should throw exception when calculation not found")
    void shouldThrowExceptionWhenCalculationNotFound() {
        // Arrange
        Long calculationId = 999L;

        when(premiumCalculationRepository.findById(calculationId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.getCalculationById(calculationId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Premium calculation not found with ID: " + calculationId);

        verify(premiumCalculationRepository).findById(calculationId);
    }

    @Test
    @DisplayName("should get average premium by vehicle type")
    void shouldGetAveragePremiumByVehicleType() {
        // Arrange
        String vehicleType = "CAR";
        BigDecimal averagePremium = new BigDecimal("2.50");

        when(premiumCalculationRepository.getAveragePremiumByVehicleType(vehicleType)).thenReturn(averagePremium);

        // Act
        BigDecimal result = service.getAveragePremiumByVehicleType(vehicleType);

        // Assert
        assertThat(result).isEqualByComparingTo("2.50");

        verify(premiumCalculationRepository).getAveragePremiumByVehicleType(vehicleType);
    }

    @Test
    @DisplayName("should return null when no average premium found for vehicle type")
    void shouldReturnNullWhenNoAveragePremiumFoundForVehicleType() {
        // Arrange
        String vehicleType = "UNKNOWN";

        when(premiumCalculationRepository.getAveragePremiumByVehicleType(vehicleType)).thenReturn(null);

        // Act
        BigDecimal result = service.getAveragePremiumByVehicleType(vehicleType);

        // Assert
        assertThat(result).isNull();

        verify(premiumCalculationRepository).getAveragePremiumByVehicleType(vehicleType);
    }
}
