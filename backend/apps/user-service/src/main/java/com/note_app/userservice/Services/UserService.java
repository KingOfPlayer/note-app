package com.note_app.userservice.Services;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ConflictException;
import com.note_app.commonutils.exception.ErrorMessages;
import com.note_app.commonutils.exception.NotFoundException;
import com.note_app.commonutils.exception.UnauthorizedException;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Repositories.UserRepository;
import com.note_app.userservice.dto.UpdateUserRequest;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository,
                       CryptoService cryptoService,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.jwtService = jwtService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(User user) {
        if (user.email() == null || user.email().isBlank()) {
            throw new BadRequestException(ErrorMessages.USER_EMAIL_BLANK);
        }
        if (user.password() == null || user.password().length() < 6) {
            throw new BadRequestException(ErrorMessages.USER_PASSWORD_SHORT);
        }
        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw new ConflictException(ErrorMessages.USER_EMAIL_EXISTS);
        }
        User toSave = new User(
                null,
                user.name(),
                user.email(),
                cryptoService.hashPassword(user.password()),
                user.role() != null ? user.role() : "USER"
        );
        return userRepository.save(toSave);
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.withId(ErrorMessages.USER_NOT_FOUND, id)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(ErrorMessages.withId(ErrorMessages.USER_NOT_FOUND, id));
        }
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(String id, UpdateUserRequest request) {
        User existing = getUserById(id);
        String name = request.getName() != null ? request.getName() : existing.name();
        String email = request.getEmail() != null ? request.getEmail() : existing.email();
        if (!email.equals(existing.email()) && userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ErrorMessages.USER_EMAIL_EXISTS);
        }
        User updated = new User(existing.id(), name, email, existing.password(), existing.role());
        return userRepository.save(updated);
    }

    @Override
    public String login(User user) {
        if (user.email() == null || user.password() == null) {
            throw new BadRequestException(ErrorMessages.AUTH_INVALID_CREDENTIALS);
        }
        User existing = userRepository.findByEmail(user.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorMessages.AUTH_INVALID_CREDENTIALS));
        if (!cryptoService.verifyPassword(user.password(), existing.password())) {
            throw new UnauthorizedException(ErrorMessages.AUTH_INVALID_CREDENTIALS);
        }
        return jwtService.generateToken(existing);
    }
}
