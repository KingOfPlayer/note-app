package com.note_app.userservice.Services;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.NotFoundExecption;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Repositories.UserRepository;
import com.note_app.userservice.dto.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(User user) {
        if (user.email() == null || user.email().isBlank()) {
            throw new BadRequestException("E-posta bos olamaz");
        }
        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw new BadRequestException("Bu e-posta zaten kayitli");
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundExecption("Kullanici bulunamadi: " + id));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundExecption("Kullanici bulunamadi: " + id);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(String id, UpdateUserRequest request) {
        User existing = getUserById(id);
        String name = request.getName() != null ? request.getName() : existing.name();
        String email = request.getEmail() != null ? request.getEmail() : existing.email();
        if (!email.equals(existing.email()) && userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Bu e-posta zaten kayitli");
        }
        User updated = new User(existing.id(), name, email, existing.password(), existing.role());
        return userRepository.save(updated);
    }
}
