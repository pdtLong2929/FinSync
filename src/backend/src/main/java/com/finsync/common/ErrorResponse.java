package com.finsync.common;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        Map<String, String> validationErrors
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now(), null);
    }

    public static ErrorResponse withValidation(int status, String error, String message, Map<String, String> validationErrors) {
        return new ErrorResponse(status, error, message, Instant.now(), validationErrors);
    }
}
