package com.note_app.userservice.Services;

import com.note_app.userservice.Entities.Models.User;

public interface IJwtService {
    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, String userEmail);
}