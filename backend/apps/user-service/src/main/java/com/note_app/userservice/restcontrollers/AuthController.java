package com.note_app.userservice.restcontrollers;

import com.note_app.commonutils.exception.UnauthorizedException;
import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.userservice.services.IUserService;
import com.note_app.userservice.dto.AuthResponse;
import com.note_app.userservice.dto.LoginRequest;
import com.note_app.userservice.dto.RegisterRequest;
import com.note_app.userservice.dto.UserResponse;
import com.note_app.userservice.entities.User;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User candidate = new User(null, request.getName(), request.getEmail(), request.getPassword(), "USER");
        User created = userService.registerUser(candidate);

        User loginInput = new User(null, null, request.getEmail(), request.getPassword(), null);
        String token = userService.login(loginInput);

        AuthResponse body = new AuthResponse(UserResponse.from(created), token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(body, "Kayit tamamlandi"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        User loginInput = new User(null, null, request.getEmail(), request.getPassword(), null);
        String token = userService.login(loginInput);

        User existing = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("E-posta veya sifre hatali"));

        AuthResponse body = new AuthResponse(UserResponse.from(existing), token);
        return ResponseEntity.ok(ApiResponse.ok(body, "Giris basarili"));
    }
}
