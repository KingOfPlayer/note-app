package com.note_app.userservice.RestControllers;

import com.note_app.commonutils.authguard.AuthGuard;
import com.note_app.commonutils.authguard.UserRoles;
import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.generic.ApiResponse;
import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.Services.IUserService;
import com.note_app.userservice.dto.UpdateUserRequest;
import com.note_app.userservice.dto.UserResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final IUserService userService;

    public UserRestController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @AuthGuard(UserRoles.ADMIN)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/me")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<UserResponse>> me(@RequestHeader("X-User-Id") String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }

    @GetMapping("/{id}")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<UserResponse>> getOne(@PathVariable("id") String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }

    @PutMapping("/{id}")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateUserRequest request) {
        if (!userId.equals(id)) {
            throw new BadRequestException("Sadece kendi profilinizi guncelleyebilirsiniz");
        }
        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(updated), "Profil guncellendi"));
    }

    @DeleteMapping("/{id}")
    @AuthGuard(UserRoles.USER)
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") String id) {
        if (!userId.equals(id)) {
            throw new BadRequestException("Sadece kendi hesabinizi silebilirsiniz");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Hesap silindi"));
    }
}
