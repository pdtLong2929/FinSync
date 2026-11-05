package com.finsync.security;

import com.finsync.exception.ApiException;
import com.finsync.exception.ApiErrorWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokens tokens;
    private final TokenBlacklist blacklist;
    private final SessionValidator sessions;
    private final ApiErrorWriter errors;
    public JwtAuthenticationFilter(JwtTokens tokens, TokenBlacklist blacklist, SessionValidator sessions, ApiErrorWriter errors) {
        this.tokens = tokens; this.blacklist = blacklist; this.sessions = sessions; this.errors = errors;
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equals(request.getMethod()) && List.of("/api/v1/auth/register", "/api/v1/auth/login",
                "/api/v1/auth/refresh").contains(request.getServletPath());
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer ")) throw ApiException.unauthorized();
                var principal = tokens.verify(header.substring(7));
                if (blacklist.contains(principal.jwtId())) throw ApiException.unauthorized();
                sessions.validate(principal);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));
                SecurityContextHolder.setContext(context);
            } catch (ApiException ex) {
                SecurityContextHolder.clearContext(); errors.write(response, ex); return;
            } catch (DataAccessException ex) {
                SecurityContextHolder.clearContext();
                errors.write(response, new ApiException(503, "AUTH_SERVICE_UNAVAILABLE", "Authentication service unavailable"));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
