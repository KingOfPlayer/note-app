package com.note_app.gatewayservice.Services;

public interface IJwtService {
    boolean isTokenValid(String token);
}