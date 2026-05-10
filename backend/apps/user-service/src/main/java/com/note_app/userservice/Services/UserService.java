package com.note_app.userservice.Services;

import com.note_app.commonutils.exception.ConflictException;
import com.note_app.commonutils.exception.NotFoundExecption;
import com.note_app.commonutils.exception.UnauthorizedException;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(User user) {
        
        Optional<User> existingUser = userRepository.findByEmail(user.email());

        if (existingUser.isPresent()) {
            throw new ConflictException("Email already in use");
        }

        User newUser = new User(
            null,
            user.name(),
            user.email(),
            cryptoService.hashPassword(user.password()),
            "USER"
        );

        return userRepository.save(newUser);
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundExecption("User not found"));
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public String login(User user) {
        User existingUser = userRepository.findByEmail(user.email())
            .orElseThrow(() -> new NotFoundExecption("User not found"));

        if (!cryptoService.verifyPassword(user.password(), existingUser.password())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtService.generateToken(existingUser);
    }
}