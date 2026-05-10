package com.note_app.userservice.Services;

import com.note_app.userservice.Entities.Models.User;
import com.note_app.userservice.dto.LoginRequest;
import com.note_app.userservice.dto.RegisterRequest;

public interface IAuthService {

    User register(RegisterRequest request);

    User login(LoginRequest request);
}
