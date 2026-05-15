package com.note_app.userservice.services;

public interface ICryptoService {
     String hashPassword(String password);
     boolean verifyPassword(String rawPassword, String hashedPassword);
};