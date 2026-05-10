package com.practice.premiumservice.util;

import com.practice.premiumservice.model.PostalCodeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PostalCodeDataLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(PostalCodeDataLoader.class);
    private static final String CSV_FILE = "postcodes.csv";
    
    private final Map<String, PostalCodeData> postalCodeMap = new ConcurrentHashMap<>();
    private final Map<String, List<PostalCodeData>> stateToPostalCodesMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        try {
            loadPostalCodeData();
            logger.info("Successfully loaded {} postal code records", postalCodeMap.size());
        } catch (IOException e) {
            logger.error("Failed to load postal code data", e);
            throw new RuntimeException("Could not initialize postal code data", e);
        }
    }
    
    private void loadPostalCodeData() throws IOException {
        var resource = new ClassPathResource(CSV_FILE);
        
        try (var inputStream = resource.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            List<PostalCodeData> postalCodes = reader.lines()
                .skip(1) // Skip header
                .map(this::parseCsvLine)
                .filter(this::isValidRecord)
                .toList();
            
            // Build lookup maps for O(1) access
            postalCodes.forEach(code -> {
                postalCodeMap.put(code.getPostalCode(), code);
                stateToPostalCodesMap.computeIfAbsent(code.getState(), k -> new java.util.ArrayList<>()).add(code);
            });
            
            // Make the lists unmodifiable for thread safety
            stateToPostalCodesMap.replaceAll((k, v) -> Collections.unmodifiableList(v));
        }
    }
    
    private PostalCodeData parseCsvLine(String line) {
        String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        
        if (fields.length < 16) {
            return null;
        }
        
        try {
            return new PostalCodeData(
                removeQuotes(fields[0]),
                removeQuotes(fields[1]),
                removeQuotes(fields[2]),
                removeQuotes(fields[3]),
                removeQuotes(fields[4]),
                removeQuotes(fields[5]),
                removeQuotes(fields[6]),
                removeQuotes(fields[7]),
                removeQuotes(fields[8]),
                removeQuotes(fields[9]),
                parseDouble(fields[10]),
                parseDouble(fields[11]),
                removeQuotes(fields[12]),
                removeQuotes(fields[13]),
                parseBoolean(fields[14]),
                removeQuotes(fields[15])
            );
        } catch (Exception e) {
            logger.debug("Failed to parse line: {}", line, e);
            return null;
        }
    }
    
    private boolean isValidRecord(PostalCodeData data) {
        return data != null && 
               data.getPostalCode() != null && 
               !data.getPostalCode().trim().isEmpty() &&
               data.getState() != null &&
               "A".equals(data.active());
    }
    
    private String removeQuotes(String value) {
        return value != null ? value.replace("\"", "").trim() : null;
    }
    
    private Double parseDouble(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Double.parseDouble(value.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value);
    }
    
    public PostalCodeData findByPostalCode(String postalCode) {
        return postalCodeMap.get(postalCode);
    }
    
    public List<PostalCodeData> findByState(String state) {
        return stateToPostalCodesMap.getOrDefault(state, Collections.emptyList());
    }
    
    public boolean isPostalCodeValid(String postalCode) {
        return postalCodeMap.containsKey(postalCode);
    }
    
    public Map<String, Integer> getStateStatistics() {
        return stateToPostalCodesMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().size()
            ));
    }
}
