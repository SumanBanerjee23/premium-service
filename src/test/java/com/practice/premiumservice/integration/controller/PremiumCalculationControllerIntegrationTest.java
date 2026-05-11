package com.practice.premiumservice.integration.controller;

import com.practice.premiumservice.model.PostalCodeData;
import com.practice.premiumservice.repository.CustomerRequestRepository;
import com.practice.premiumservice.repository.PremiumCalculationRepository;
import com.practice.premiumservice.util.PostalCodeDataLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PremiumCalculationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private PremiumCalculationRepository premiumCalculationRepository;

    @MockitoBean
    private PostalCodeDataLoader postalCodeDataLoader;

    
    @BeforeEach
    void setUp() {
        // Clean up database before each test
        customerRequestRepository.deleteAll();
        premiumCalculationRepository.deleteAll();
        
        // Setup common mock behavior
        setupPostalCodeMocks();
    }

    @AfterEach
    void tearDown() {
        // Clean up database after each test
        customerRequestRepository.deleteAll();
        premiumCalculationRepository.deleteAll();
    }

    private void setupPostalCodeMocks() {
        // Mock valid postal codes
        when(postalCodeDataLoader.isPostalCodeValid("79189")).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid("10115")).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid("20095")).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid("80331")).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid(" 79189 ")).thenReturn(true);
        when(postalCodeDataLoader.isPostalCodeValid("99999")).thenReturn(false);

        // Mock postal code data
        when(postalCodeDataLoader.findByPostalCode("79189")).thenReturn(
            new PostalCodeData(
                "DE", "BW", "Baden-Württemberg", null, "Deutschland", "Bad Krozingen",
                "79189", "Bad Krozingen", null, null, null, null, null, null, null, "1"
            )
        );

        when(postalCodeDataLoader.findByPostalCode("10115")).thenReturn(
            new PostalCodeData(
                "DE", "BE", "Berlin", null, "Deutschland", "Berlin",
                "10115", "Berlin", null, null, null, null, null, null, null, "1"
            )
        );

        when(postalCodeDataLoader.findByPostalCode("20095")).thenReturn(
            new PostalCodeData(
                "DE", "HH", "Hamburg", null, "Deutschland", "Hamburg",
                "20095", "Hamburg", null, null, null, null, null, null, null, "1"
            )
        );

        when(postalCodeDataLoader.findByPostalCode("80331")).thenReturn(
            new PostalCodeData(
                "DE", "BY", "Bayern", null, "Deutschland", "München",
                "80331", "München", null, null, null, null, null, null, null, "1"
            )
        );

        when(postalCodeDataLoader.findByPostalCode(" 79189 ")).thenReturn(
            new PostalCodeData(
                "DE", "BW", "Baden-Württemberg", null, "Deutschland", "Bad Krozingen",
                " 79189 ", "Bad Krozingen", null, null, null, null, null, null, null, "1"
            )
        );
    }

    @Test
    @DisplayName("should persist premium calculation into real H2 database")
    void shouldPersistPremiumCalculationIntoRealH2Database() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.postalCode").value("79189"))
                .andExpect(jsonPath("$.annualMileage").value(15000))
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.state").value("Baden-Württemberg"))
                .andExpect(jsonPath("$.city").value("Bad Krozingen"))
                .andExpect(jsonPath("$.mileageFactor").value(1.5))
                .andExpect(jsonPath("$.vehicleTypeFactor").value(1.0))
                .andExpect(jsonPath("$.regionFactor").value(1.2))
                .andExpect(jsonPath("$.calculatedPremium").value(1.80))
                .andExpect(jsonPath("$.calculatedAt").exists());

        // Verify data persistence in H2 database
        assertThat(customerRequestRepository.count()).isEqualTo(1);
        assertThat(premiumCalculationRepository.count()).isEqualTo(1);

        var savedCustomerRequest = customerRequestRepository.findAll().getFirst();
        assertThat(savedCustomerRequest.getPostalCode()).isEqualTo("79189");
        assertThat(savedCustomerRequest.getAnnualMileage()).isEqualTo(15000);
        assertThat(savedCustomerRequest.getVehicleType()).isEqualTo("CAR");
        assertThat(savedCustomerRequest.getState()).isEqualTo("Baden-Württemberg");
        assertThat(savedCustomerRequest.getCity()).isEqualTo("Bad Krozingen");

        var savedCalculation = premiumCalculationRepository.findAll().getFirst();
        assertThat(savedCalculation.getCalculatedPremium()).isEqualByComparingTo("1.80");
        assertThat(savedCalculation.getMileageFactor()).isEqualByComparingTo("1.5");
        assertThat(savedCalculation.getVehicleTypeFactor()).isEqualByComparingTo("1.0");
        assertThat(savedCalculation.getRegionFactor()).isEqualByComparingTo("1.2");
    }

    @Test
    @DisplayName("should return 404 for invalid postal code")
    void shouldReturn404ForInvalidPostalCode() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "99999",
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isNotFound());

        // Verify no data was persisted
        assertThat(customerRequestRepository.count()).isEqualTo(0);
        assertThat(premiumCalculationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("should return 400 for invalid request data")
    void shouldReturn400ForInvalidRequestData() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "",
                        "annualMileage": -1000,
                        "vehicleType": ""
                    }
                """))
                .andExpect(status().isBadRequest());

        // Verify no data was persisted
        assertThat(customerRequestRepository.count()).isEqualTo(0);
        assertThat(premiumCalculationRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("should retrieve saved calculation by ID")
    void shouldRetrieveSavedCalculationById() throws Exception {
        // First, create a calculation
        var createResult = mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "10115",
                        "annualMileage": 7500,
                        "vehicleType": "MOTORCYCLE"
                    }
                """))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        Long calculationId = Long.parseLong(responseContent.split("\"id\":")[1].split(",")[0].trim());

        // Act + Assert - Retrieve the calculation
        mockMvc.perform(get("/api/premium/calculations/{id}", calculationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(calculationId))
                .andExpect(jsonPath("$.postalCode").value("10115"))
                .andExpect(jsonPath("$.annualMileage").value(7500))
                .andExpect(jsonPath("$.vehicleType").value("MOTORCYCLE"))
                .andExpect(jsonPath("$.state").value("Berlin"))
                .andExpect(jsonPath("$.city").value("Berlin"))
                .andExpect(jsonPath("$.calculatedPremium").value(0.91));
    }

    @Test
    @DisplayName("should return 404 for non-existent calculation ID")
    void shouldReturn404ForNonExistentCalculationId() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/api/premium/calculations/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return all vehicle types")
    void shouldReturnAllVehicleTypes() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/api/premium/vehicle-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CAR").value(1.0))
                .andExpect(jsonPath("$.MOTORCYCLE").value(0.7))
                .andExpect(jsonPath("$.TRUCK").value(1.5))
                .andExpect(jsonPath("$.VAN").value(1.2))
                .andExpect(jsonPath("$.ELECTRIC_CAR").value(0.8))
                .andExpect(jsonPath("$.HYBRID_CAR").value(0.9))
                .andExpect(jsonPath("$.LUXURY_CAR").value(1.8))
                .andExpect(jsonPath("$.SPORTS_CAR").value(1.6))
                .andExpect(jsonPath("$.SUV").value(1.4));
    }

    @Test
    @DisplayName("should return all region factors")
    void shouldReturnAllRegionFactors() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/api/premium/region-factors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Baden-Württemberg").value(1.2))
                .andExpect(jsonPath("$.Bayern").value(1.1))
                .andExpect(jsonPath("$.Berlin").value(1.3))
                .andExpect(jsonPath("$.Brandenburg").value(0.9))
                .andExpect(jsonPath("$.Bremen").value(1.1))
                .andExpect(jsonPath("$.Hamburg").value(1.3))
                .andExpect(jsonPath("$.Hessen").value(1.1))
                .andExpect(jsonPath("$.Mecklenburg-Vorpommern").value(0.8))
                .andExpect(jsonPath("$.Niedersachsen").value(1.0))
                .andExpect(jsonPath("$.Nordrhein-Westfalen").value(1.2))
                .andExpect(jsonPath("$.Rheinland-Pfalz").value(1.0))
                .andExpect(jsonPath("$.Saarland").value(0.9))
                .andExpect(jsonPath("$.Sachsen").value(0.8))
                .andExpect(jsonPath("$.Sachsen-Anhalt").value(0.8))
                .andExpect(jsonPath("$.Schleswig-Holstein").value(1.0))
                .andExpect(jsonPath("$.Thüringen").value(0.8));
    }

    @Test
    @DisplayName("should return average premium by vehicle type")
    void shouldReturnAveragePremiumByVehicleType() throws Exception {
        // Create multiple calculations for the same vehicle type
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "80331",
                        "annualMileage": 5000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "80331",
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated());

        // Act + Assert
        mockMvc.perform(get("/api/premium/statistics/average-premium/{vehicleType}", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.averagePremium").exists());
    }

    @Test
    @DisplayName("should return 404 for average premium of vehicle type with no calculations")
    void shouldReturn404ForAveragePremiumOfVehicleTypeWithNoCalculations() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/api/premium/statistics/average-premium/{vehicleType}", "UNKNOWN_TYPE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 404 for missing calculation ID")
    void shouldReturn404ForMissingCalculationId() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/api/premium/calculations/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 for unknown vehicle type")
    void shouldReturn400ForUnknownVehicleType() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": "UNKNOWN_VEHICLE"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should handle zero mileage edge case")
    void shouldHandleZeroMileageEdgeCase() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 0,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(0.5))
                .andExpect(jsonPath("$.calculatedPremium").value(0.60));
    }

    @Test
    @DisplayName("should handle maximum allowed mileage")
    void shouldHandleMaximumAllowedMileage() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 1000000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(2.0))
                .andExpect(jsonPath("$.calculatedPremium").value(2.40));
    }

    @Test
    @DisplayName("should return 400 for mileage exceeding maximum")
    void shouldReturn400ForMileageExceedingMaximum() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 1000001,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should handle vehicle type case insensitivity")
    void shouldHandleVehicleTypeCaseInsensitivity() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": "car"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleType").value("car"))
                .andExpect(jsonPath("$.vehicleTypeFactor").value(1.0));
    }

    @Test
    @DisplayName("should handle vehicle type with spaces")
    void shouldHandleVehicleTypeWithSpaces() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": "  CAR  "
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleType").value("  CAR  "))
                .andExpect(jsonPath("$.vehicleTypeFactor").value(1.0));
    }

    @Test
    @DisplayName("should return 400 for null vehicle type")
    void shouldReturn400ForNullVehicleType() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": null
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for empty vehicle type")
    void shouldReturn400ForEmptyVehicleType() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": ""
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for null postal code")
    void shouldReturn400ForNullPostalCode() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": null,
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for empty postal code")
    void shouldReturn400ForEmptyPostalCode() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "",
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for negative mileage")
    void shouldReturn400ForNegativeMileage() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": -1,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should handle postal code with spaces")
    void shouldHandlePostalCodeWithSpaces() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": " 79189 ",
                        "annualMileage": 15000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postalCode").value(" 79189 "));
    }

    @Test
    @DisplayName("should return 400 for malformed JSON")
    void shouldReturn400ForMalformedJson() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 15000,
                        "vehicleType": "CAR",
                    }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }

    @Test
    @DisplayName("should return 400 for missing required fields")
    void shouldReturn400ForMissingRequiredFields() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should handle different mileage brackets correctly")
    void shouldHandleDifferentMileageBracketsCorrectly() throws Exception {
        // Test exactly 5000 mileage (0.5 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 5000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(0.5))
                .andExpect(jsonPath("$.calculatedPremium").value(0.60));

        // Test exactly 5001 mileage (1.0 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 5001,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(1.0))
                .andExpect(jsonPath("$.calculatedPremium").value(1.20));

        // Test exactly 10000 mileage (1.0 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 10000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(1.0))
                .andExpect(jsonPath("$.calculatedPremium").value(1.20));

        // Test exactly 10001 mileage (1.5 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 10001,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(1.5))
                .andExpect(jsonPath("$.calculatedPremium").value(1.80));

        // Test exactly 20000 mileage (1.5 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 20000,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(1.5))
                .andExpect(jsonPath("$.calculatedPremium").value(1.80));

        // Test exactly 20001 mileage (2.0 factor)
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 20001,
                        "vehicleType": "CAR"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mileageFactor").value(2.0))
                .andExpect(jsonPath("$.calculatedPremium").value(2.40));
    }

    @Test
    @DisplayName("should handle different vehicle types correctly")
    void shouldHandleDifferentVehicleTypesCorrectly() throws Exception {
        // Test different vehicle types
        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 10000,
                        "vehicleType": "MOTORCYCLE"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleTypeFactor").value(0.7))
                .andExpect(jsonPath("$.calculatedPremium").value(0.84));

        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 10000,
                        "vehicleType": "TRUCK"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleTypeFactor").value(1.5))
                .andExpect(jsonPath("$.calculatedPremium").value(1.80));

        mockMvc.perform(post("/api/premium/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "postalCode": "79189",
                        "annualMileage": 10000,
                        "vehicleType": "SUV"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleTypeFactor").value(1.4))
                .andExpect(jsonPath("$.calculatedPremium").value(1.68));
    }
}
