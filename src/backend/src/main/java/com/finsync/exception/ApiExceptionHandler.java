package com.finsync.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    private final ApiErrorWriter errors;
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> api(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).header("Cache-Control", "no-store").body(errors.body(ex));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> malformed(HttpMessageNotReadableException ex) {
        return api(new ApiException(400, "VALIDATION_ERROR", "Invalid JSON or unsupported field"));
    }
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> conflict(OptimisticLockingFailureException ex) {
        return api(new ApiException(409, "PROFILE_VERSION_CONFLICT", "Profile changed; reload before saving"));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> integrity(DataIntegrityViolationException ex) {
        // The database remains authoritative for concurrent registration attempts.
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException constraint
                    && "uk_users_email".equals(constraint.getConstraintName())) {
                return api(new ApiException(409, "EMAIL_ALREADY_EXISTS", "Email is already registered"));
            }
            cause = cause.getCause();
        }
        return api(new ApiException(409, "DATA_CONFLICT", "Data conflicts with an existing record"));
    }
    @ExceptionHandler(DataAccessResourceFailureException.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> unavailable(DataAccessResourceFailureException ex) {
        return api(new ApiException(503, "AUTH_SERVICE_UNAVAILABLE", "Authentication service unavailable"));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorWriter.ErrorBody> unexpected(Exception ex) {
        var body = errors.body(new ApiException(500, "INTERNAL_ERROR", "Unexpected server error"));
        log.error("Request failed: traceId={}, exceptionType={}", body.traceId(), ex.getClass().getName());
        return ResponseEntity.internalServerError().body(body);
    }
}
