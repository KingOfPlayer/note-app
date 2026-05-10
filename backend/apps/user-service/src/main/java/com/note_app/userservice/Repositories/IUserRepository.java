package com.note_app.userservice.Repositories;

import java.util.List;
import java.util.Optional;

import com.note_app.userservice.Entities.Models.User;

public interface IUserRepository {
    List<User> findAll();
    User save(User user);
    Optional<User> findById(String id);
    void deleteById(String id);
}
