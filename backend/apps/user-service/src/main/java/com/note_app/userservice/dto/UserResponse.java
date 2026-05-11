package com.note_app.userservice.dto;

import com.note_app.userservice.Entities.Models.User;

public class UserResponse {

    private final String id;
    private final String name;
    private final String email;
    private final String role;

    public UserResponse(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.role());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
