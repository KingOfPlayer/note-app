package com.note_app.userservice.services;

import com.note_app.userservice.dto.UpdateUserRequest;
import com.note_app.userservice.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getAllUsers();
    User registerUser(User user);
    User getUserById(String id);
    Optional<User> findByEmail(String email);
    void deleteUser(String id);
    User updateUser(String id, UpdateUserRequest request);
    String login(User user);
}
