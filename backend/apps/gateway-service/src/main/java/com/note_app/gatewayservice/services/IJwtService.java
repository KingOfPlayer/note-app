package com.note_app.gatewayservice.services;

public interface IJwtService {
    boolean isTokenValid(String token);
}