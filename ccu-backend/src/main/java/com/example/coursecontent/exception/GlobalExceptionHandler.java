package com.example.coursecontent.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.coursecontent.dto.response.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleInvalidFileTypeException(InvalidFileTypeException ex) {
        logger.warn("Invalid file type: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_FILE_TYPE", ex.getMessage());
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleFileTooLargeException(FileTooLargeException ex) {
        logger.warn("File too large: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleNotFoundException(NotFoundException ex) {
        logger.warn("Not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleStorageException(StorageException ex) {
        logger.error("Storage error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "STORAGE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleUnauthorizedException(UnauthorizedException ex) {
        logger.warn("Unauthorized: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                LocalDateTime.now()
        );
        errorResponse.setDetails(errors);
        
        StandardResponse<ErrorResponse> response = StandardResponse.error("Validation failed");
        response.setData(errorResponse);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException ex) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Invalid email or password");
    }

    @ExceptionHandler(org.springframework.data.mapping.PropertyReferenceException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handlePropertyReferenceException(org.springframework.data.mapping.PropertyReferenceException ex) {
        logger.warn("Invalid property reference: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_PROPERTY", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleGenericException(Exception ex) {
        logger.error("Unexpected error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> handleMaxUploadSizeExceededException(org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
        logger.warn("Max upload size exceeded: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "File size exceeds the maximum allowed limit of 50MB.");
    }

    private ResponseEntity<StandardResponse<ErrorResponse>> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                error,
                LocalDateTime.now()
        );
        
        StandardResponse<ErrorResponse> response = StandardResponse.error(message);
        response.setData(errorResponse);
        
        return new ResponseEntity<>(response, status);
    }

    public static class ErrorResponse {
        private int status;
        private String error;
        private LocalDateTime timestamp;
        private Map<String, String> details;

        public ErrorResponse(int status, String error, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public Map<String, String> getDetails() { return details; }
        public void setDetails(Map<String, String> details) { this.details = details; }
    }
}
