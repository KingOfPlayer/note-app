package com.note_app.userservice.RestControllers;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private IUserService userService; // Inject the interface

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getOne(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        if (user.email() == null || user.password() == null) {
            throw new BadRequestException("Email and password must be provided");
        }
        
        Map<String, String> loginResponse = Map.of("token", userService.login(user));

        return ResponseEntity.ok(loginResponse);
    }
}