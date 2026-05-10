package com.note_app.userservice.Services;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.UnauthorizedException;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Repositories.UserRepository;
import com.note_app.userservice.dto.LoginRequest;
import com.note_app.userservice.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Bu e-posta zaten kayitli");
        }
        String hashed = passwordEncoder.encode(request.getPassword());
        User newUser = new User(null, request.getName(), request.getEmail(), hashed, "USER");
        return userRepository.save(newUser);
    }

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("E-posta veya sifre hatali"));
        if (!passwordEncoder.matches(request.getPassword(), user.password())) {
            throw new UnauthorizedException("E-posta veya sifre hatali");
        }
        return user;
    }
}
