package com.finsync.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final String code;
    private final Map<String, String> fieldErrors;
    public ApiException(int status, String code, String message) {
        this(status, code, message, Map.of());
    }
    public ApiException(int status, String code, String message, Map<String, String> fields) {
        super(message);
        this.status = status;
        this.code = code;
        this.fieldErrors = Map.copyOf(fields);
    }
    public static ApiException invalid(String field, String message) {
        return new ApiException(400, "VALIDATION_ERROR", "Invalid request", Map.of(field, message));
    }
    public static ApiException unauthorized() {
        return new ApiException(401, "UNAUTHORIZED", "Authentication required");
    }
}
