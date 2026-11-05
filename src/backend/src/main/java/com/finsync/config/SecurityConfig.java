package com.finsync.config;

import com.finsync.exception.*;
import com.finsync.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean SecurityFilterChain security(HttpSecurity http, JwtTokens tokens, TokenBlacklist blacklist,
                                       SessionValidator sessions, ApiErrorWriter errors) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) // Native API: credentials are Bearer/JSON, not browser cookies.
                .formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable).requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers(HttpMethod.POST,
                        "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> errors.write(res, ApiException.unauthorized()))
                        .accessDeniedHandler((req, res, ex) -> errors.write(res,
                                new ApiException(403, "FORBIDDEN", "Access denied"))))
                .addFilterBefore(new JwtAuthenticationFilter(tokens, blacklist, sessions, errors), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}