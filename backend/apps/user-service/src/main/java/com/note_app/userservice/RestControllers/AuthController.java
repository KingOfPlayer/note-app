package com.note_app.userservice.RestControllers;

import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Services.IAuthService;
import com.note_app.userservice.dto.AuthResponse;
import com.note_app.userservice.dto.LoginRequest;
import com.note_app.userservice.dto.RegisterRequest;
import com.note_app.userservice.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        AuthResponse body = new AuthResponse(UserResponse.from(user), buildToken(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(body, "Kayit tamamlandi"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        AuthResponse body = new AuthResponse(UserResponse.from(user), buildToken(user));
        return ResponseEntity.ok(ApiResponse.ok(body, "Giris basarili"));
    }

    private String buildToken(User user) {
        String raw = user.id() + ":" + user.role();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
    }
}
