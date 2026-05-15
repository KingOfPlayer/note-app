package com.note_app.userservice.services;

import com.note_app.userservice.entities.User;

public interface IJwtService {
    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, String userEmail);
}