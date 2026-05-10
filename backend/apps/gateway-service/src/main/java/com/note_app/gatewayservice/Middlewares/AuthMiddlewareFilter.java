package com.note_app.gatewayservice.Middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.note_app.gatewayservice.Services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthMiddlewareFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ObjectMapper objectMapper;

    public AuthMiddlewareFilter(JWTService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        // Remove x-user-* headers to prevent spoofing
        mutableRequest.removeHeader("x-user-id");
        mutableRequest.removeHeader("x-user-role");
        mutableRequest.removeHeader("x-user-email");

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length()).trim();

            if (!token.isEmpty()) {
                try {
                    JWTService.DecodedToken decoded = jwtService.verifyAndDecode(token);

                    if (decoded.userId() != null) {
                        mutableRequest.putHeader("x-user-id", decoded.userId());
                    }
                    if (decoded.role() != null) {
                        mutableRequest.putHeader("x-user-role", decoded.role());
                    }
                    if (decoded.email() != null) {
                        mutableRequest.putHeader("x-user-email", decoded.email());
                    }
                } catch (Exception ex) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(), Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", "Invalid token"
                    ));
                    return;
                }
            }
        }

        filterChain.doFilter(mutableRequest, response);
    }
}
