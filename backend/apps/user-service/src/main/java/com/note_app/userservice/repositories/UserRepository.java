package com.note_app.userservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.note_app.userservice.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Adding a helpful custom query for login or profile lookups
    Optional<User> findByEmail(String email);
}
