package com.practice.premiumservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostalCodeData(
    String iso3166Alpha2,
    String iso3166Alpha2RegionCode,
    String region1, // State
    String region2,
    String region3, // Country
    String region4, // City/Town
    String postleitzahl, // ZIP code
    String ort, // Location
    String area1,
    String area2,
    Double latitude,
    Double longitude,
    String zeitzone,
    String utc,
    Boolean sommerzeit,
    String active
) {
    
    public String getState() {
        return region1;
    }
    
    public String getPostalCode() {
        return postleitzahl;
    }
    
    public String getCity() {
        return ort;
    }
}
