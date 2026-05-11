package com.practice.premiumservice.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "timestamp": "2024-01-15T10:30:00",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Validation failed",
                  "errors": {
                    "annualMileage": "Annual mileage cannot be negative",
                    "postalCode": "Postal code is required"
                  }
                }
                """
            )
        )
    )
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors
        );
        
        logger.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "timestamp": "2024-01-15T10:30:00",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Premium calculation not found with ID: 999",
                  "errors": null
                }
                """
            )
        )
    )
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null
        );
        
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidVehicleTypeException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Invalid vehicle type",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "timestamp": "2024-01-15T10:30:00",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Unknown vehicle type: UNKNOWN_VEHICLE",
                  "errors": null
                }
                """
            )
        )
    )
    public ResponseEntity<ErrorResponse> handleInvalidVehicleTypeException(InvalidVehicleTypeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        logger.warn("Invalid vehicle type: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Malformed JSON request",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "timestamp": "2024-01-15T10:30:00",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Malformed JSON request",
                  "errors": null
                }
                """
            )
        )
    )
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON request",
            null
        );
        
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "timestamp": "2024-01-15T10:30:00",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "An unexpected error occurred",
                  "errors": null
                }
                """
            )
        )
    )
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            null
        );
        
        logger.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @Schema(description = "Error response structure")
    public record ErrorResponse(
        @Schema(description = "Timestamp of the error")
        LocalDateTime timestamp,
        
        @Schema(description = "HTTP status code")
        Integer status,
        
        @Schema(description = "Error type")
        String error,
        
        @Schema(description = "Error message")
        String message,
        
        @Schema(description = "Field validation errors")
        Map<String, String> errors
    ) {
        public ErrorResponse(LocalDateTime timestamp, Integer status, String message, Map<String, String> errors) {
            this(timestamp, status, getErrorType(status), message, errors);
        }
        
        private static String getErrorType(Integer status) {
            return switch (status) {
                case 400 -> "Bad Request";
                case 404 -> "Not Found";
                case 500 -> "Internal Server Error";
                default -> "Error";
            };
        }
    }
}
