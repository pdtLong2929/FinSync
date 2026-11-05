package com.finsync.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class ApiErrorWriter {
    public record ErrorBody(String code, String message, Map<String, String> fieldErrors, String traceId) {}
    private final ObjectMapper mapper;
    public ApiErrorWriter(ObjectMapper mapper) { this.mapper = mapper; }
    public ErrorBody body(ApiException ex) {
        return new ErrorBody(ex.getCode(), ex.getMessage(), ex.getFieldErrors(), UUID.randomUUID().toString());
    }
    public void write(HttpServletResponse response, ApiException ex) throws IOException {
        response.setStatus(ex.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(mapper.writeValueAsString(body(ex)));
    }
}
