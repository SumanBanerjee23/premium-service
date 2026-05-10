package com.practice.premiumservice.unit.service;

import com.practice.premiumservice.model.PostalCodeData;
import com.practice.premiumservice.service.RegionalDataService;
import com.practice.premiumservice.util.PostalCodeDataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalDataServiceTest {

    @Mock
    private PostalCodeDataLoader postalCodeDataLoader;

    @InjectMocks
    private RegionalDataService service;

    @Test
    @DisplayName("should return region factor for valid postal code")
    void shouldReturnRegionFactorForValidPostalCode() {
        // Arrange
        String postalCode = "79189";
        PostalCodeData postalCodeData = new PostalCodeData(
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

        when(postalCodeDataLoader.findByPostalCode(postalCode)).thenReturn(postalCodeData);

        // Act
        BigDecimal result = service.getRegionFactor(postalCode);

        // Assert
        assertThat(result).isEqualByComparingTo("1.2"); // Baden-Württemberg factor

        verify(postalCodeDataLoader).findByPostalCode(postalCode);
    }

    @Test
    @DisplayName("should return default factor for unknown postal code")
    void shouldReturnDefaultFactorForUnknownPostalCode() {
        // Arrange
        String postalCode = "99999";

        when(postalCodeDataLoader.findByPostalCode(postalCode)).thenReturn(null);

        // Act
        BigDecimal result = service.getRegionFactor(postalCode);

        // Assert
        assertThat(result).isEqualByComparingTo("1.0"); // Default factor

        verify(postalCodeDataLoader).findByPostalCode(postalCode);
    }

    @Test
    @DisplayName("should return default factor for unknown state")
    void shouldReturnDefaultFactorForUnknownState() {
        // Arrange
        String postalCode = "12345";
        PostalCodeData postalCodeData = new PostalCodeData(
            "DE",           // iso3166Alpha2
            "XX",           // iso3166Alpha2RegionCode
            "Unknown State", // region1 (State)
            null,           // region2
            "Deutschland",  // region3 (Country)
            "Unknown City", // region4 (City/Town)
            "12345",        // postleitzahl (ZIP code)
            "Unknown City", // ort (Location)
            null,           // area1
            null,           // area2
            null,           // latitude
            null,           // longitude
            null,           // zeitzone
            null,           // utc
            null,           // sommerzeit
            "1"             // active
        );

        when(postalCodeDataLoader.findByPostalCode(postalCode)).thenReturn(postalCodeData);

        // Act
        BigDecimal result = service.getRegionFactor(postalCode);

        // Assert
        assertThat(result).isEqualByComparingTo("1.0"); // Default factor

        verify(postalCodeDataLoader).findByPostalCode(postalCode);
    }

    @Test
    @DisplayName("should return vehicle type factor for known vehicle type")
    void shouldReturnVehicleTypeFactorForKnownVehicleType() {
        // Arrange
        String vehicleType = "CAR";

        // Act
        BigDecimal result = service.getVehicleTypeFactor(vehicleType);

        // Assert
        assertThat(result).isEqualByComparingTo("1.0");
    }

    @Test
    @DisplayName("should return vehicle type factor for known vehicle type in lowercase")
    void shouldReturnVehicleTypeFactorForKnownVehicleTypeInLowercase() {
        // Arrange
        String vehicleType = "car";

        // Act
        BigDecimal result = service.getVehicleTypeFactor(vehicleType);

        // Assert
        assertThat(result).isEqualByComparingTo("1.0");
    }

    @Test
    @DisplayName("should return default factor for unknown vehicle type")
    void shouldReturnDefaultFactorForUnknownVehicleType() {
        // Arrange
        String vehicleType = "UNKNOWN_TYPE";

        // Act
        BigDecimal result = service.getVehicleTypeFactor(vehicleType);

        // Assert
        assertThat(result).isEqualByComparingTo("1.0"); // Default factor
    }

    @Test
    @DisplayName("should throw exception for null vehicle type")
    void shouldThrowExceptionForNullVehicleType() {
        // Act + Assert
        assertThatThrownBy(() -> service.getVehicleTypeFactor(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Vehicle type cannot be null or empty");
    }

    @Test
    @DisplayName("should throw exception for empty vehicle type")
    void shouldThrowExceptionForEmptyVehicleType() {
        // Act + Assert
        assertThatThrownBy(() -> service.getVehicleTypeFactor(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Vehicle type cannot be null or empty");
    }

    @Test
    @DisplayName("should throw exception for whitespace-only vehicle type")
    void shouldThrowExceptionForWhitespaceOnlyVehicleType() {
        // Act + Assert
        assertThatThrownBy(() -> service.getVehicleTypeFactor("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Vehicle type cannot be null or empty");
    }

    @Test
    @DisplayName("should return postal code data for valid postal code")
    void shouldReturnPostalCodeDataForValidPostalCode() {
        // Arrange
        String postalCode = "79189";
        PostalCodeData expectedData = new PostalCodeData(
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

        when(postalCodeDataLoader.findByPostalCode(postalCode)).thenReturn(expectedData);

        // Act
        PostalCodeData result = service.getPostalCodeData(postalCode);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPostalCode()).isEqualTo("79189");
        assertThat(result.getCity()).isEqualTo("Bad Krozingen");
        assertThat(result.getState()).isEqualTo("Baden-Württemberg");

        verify(postalCodeDataLoader).findByPostalCode(postalCode);
    }

    @Test
    @DisplayName("should return null for null postal code")
    void shouldReturnNullForNullPostalCode() {
        // Act
        PostalCodeData result = service.getPostalCodeData(null);

        // Assert
        assertThat(result).isNull();

        verifyNoInteractions(postalCodeDataLoader);
    }

    @Test
    @DisplayName("should return null for empty postal code")
    void shouldReturnNullForEmptyPostalCode() {
        // Act
        PostalCodeData result = service.getPostalCodeData("");

        // Assert
        assertThat(result).isNull();

        verifyNoInteractions(postalCodeDataLoader);
    }

    @Test
    @DisplayName("should return null for whitespace-only postal code")
    void shouldReturnNullForWhitespaceOnlyPostalCode() {
        // Act
        PostalCodeData result = service.getPostalCodeData("   ");

        // Assert
        assertThat(result).isNull();

        verifyNoInteractions(postalCodeDataLoader);
    }

    @Test
    @DisplayName("should check postal code validity")
    void shouldCheckPostalCodeValidity() {
        // Arrange
        String validPostalCode = "79189";
        String invalidPostalCode = "99999";

        when(postalCodeDataLoader.isPostalCodeValid(validPostalCode)).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid(invalidPostalCode)).thenReturn(false);

        // Act + Assert
        assertThat(service.isPostalCodeValid(validPostalCode)).isTrue();
        assertThat(service.isPostalCodeValid(invalidPostalCode)).isFalse();

        verify(postalCodeDataLoader).isPostalCodeValid(validPostalCode);
        verify(postalCodeDataLoader).isPostalCodeValid(invalidPostalCode);
    }

    @Test
    @DisplayName("should return all region factors")
    void shouldReturnAllRegionFactors() {
        // Act
        Map<String, BigDecimal> result = service.getAllRegionFactors();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(16); // All German federal states
        assertThat(result.get("Baden-Württemberg")).isEqualByComparingTo("1.2");
        assertThat(result.get("Bayern")).isEqualByComparingTo("1.1");
        assertThat(result.get("Berlin")).isEqualByComparingTo("1.3");
    }

    @Test
    @DisplayName("should return all vehicle type factors")
    void shouldReturnAllVehicleTypeFactors() {
        // Act
        Map<String, BigDecimal> result = service.getAllVehicleTypeFactors();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(9); // All vehicle types
        assertThat(result.get("CAR")).isEqualByComparingTo("1.0");
        assertThat(result.get("MOTORCYCLE")).isEqualByComparingTo("0.7");
        assertThat(result.get("TRUCK")).isEqualByComparingTo("1.5");
    }

    @Test
    @DisplayName("should update region factor successfully")
    void shouldUpdateRegionFactorSuccessfully() {
        // Arrange
        String state = "Test State";
        BigDecimal newFactor = new BigDecimal("1.5");

        // Act
        service.updateRegionFactor(state, newFactor);

        // Assert
        Map<String, BigDecimal> allFactors = service.getAllRegionFactors();
        assertThat(allFactors.get(state)).isEqualByComparingTo("1.5");
    }

    @Test
    @DisplayName("should throw exception when updating region factor with null state")
    void shouldThrowExceptionWhenUpdatingRegionFactorWithNullState() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateRegionFactor(null, new BigDecimal("1.5")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid state or factor");
    }

    @Test
    @DisplayName("should throw exception when updating region factor with empty state")
    void shouldThrowExceptionWhenUpdatingRegionFactorWithEmptyState() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateRegionFactor("", new BigDecimal("1.5")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid state or factor");
    }

    @Test
    @DisplayName("should throw exception when updating region factor with null factor")
    void shouldThrowExceptionWhenUpdatingRegionFactorWithNullFactor() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateRegionFactor("Test State", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid state or factor");
    }

    @Test
    @DisplayName("should throw exception when updating region factor with negative factor")
    void shouldThrowExceptionWhenUpdatingRegionFactorWithNegativeFactor() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateRegionFactor("Test State", new BigDecimal("-1.0")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid state or factor");
    }

    @Test
    @DisplayName("should throw exception when updating region factor with zero factor")
    void shouldThrowExceptionWhenUpdatingRegionFactorWithZeroFactor() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateRegionFactor("Test State", BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid state or factor");
    }

    @Test
    @DisplayName("should update vehicle type factor successfully")
    void shouldUpdateVehicleTypeFactorSuccessfully() {
        // Arrange
        String vehicleType = "TEST_VEHICLE";
        BigDecimal newFactor = new BigDecimal("1.8");

        // Act
        service.updateVehicleTypeFactor(vehicleType, newFactor);

        // Assert
        Map<String, BigDecimal> allFactors = service.getAllVehicleTypeFactors();
        assertThat(allFactors.get("TEST_VEHICLE")).isEqualByComparingTo("1.8");
    }

    @Test
    @DisplayName("should update vehicle type factor with lowercase input")
    void shouldUpdateVehicleTypeFactorWithLowercaseInput() {
        // Arrange
        String vehicleType = "test_vehicle";
        BigDecimal newFactor = new BigDecimal("1.8");

        // Act
        service.updateVehicleTypeFactor(vehicleType, newFactor);

        // Assert
        Map<String, BigDecimal> allFactors = service.getAllVehicleTypeFactors();
        assertThat(allFactors.get("TEST_VEHICLE")).isEqualByComparingTo("1.8");
    }

    @Test
    @DisplayName("should throw exception when updating vehicle type factor with null type")
    void shouldThrowExceptionWhenUpdatingVehicleTypeFactorWithNullType() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateVehicleTypeFactor(null, new BigDecimal("1.5")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid vehicle type or factor");
    }

    @Test
    @DisplayName("should throw exception when updating vehicle type factor with empty type")
    void shouldThrowExceptionWhenUpdatingVehicleTypeFactorWithEmptyType() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateVehicleTypeFactor("", new BigDecimal("1.5")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid vehicle type or factor");
    }

    @Test
    @DisplayName("should throw exception when updating vehicle type factor with null factor")
    void shouldThrowExceptionWhenUpdatingVehicleTypeFactorWithNullFactor() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateVehicleTypeFactor("TEST_TYPE", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid vehicle type or factor");
    }

    @Test
    @DisplayName("should throw exception when updating vehicle type factor with negative factor")
    void shouldThrowExceptionWhenUpdatingVehicleTypeFactorWithNegativeFactor() {
        // Act + Assert
        assertThatThrownBy(() -> service.updateVehicleTypeFactor("TEST_TYPE", new BigDecimal("-1.0")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid vehicle type or factor");
    }
}
