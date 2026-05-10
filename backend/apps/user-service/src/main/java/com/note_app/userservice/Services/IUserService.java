package com.note_app.userservice.Services;

import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.dto.UpdateUserRequest;

import java.util.List;

public interface IUserService {
    List<User> getAllUsers();
    User registerUser(User user);
    User getUserById(String id);
    void deleteUser(String id);
    User updateUser(String id, UpdateUserRequest request);
}
