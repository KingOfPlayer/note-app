package com.note_app.userservice.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public record User (
    @Id
    String id,
    String name,
    String email,
    String password, // In a real application, this should be hashed and salted
    String role // e.g., "USER", "ADMIN"
) {
}