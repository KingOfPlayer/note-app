package com.note_app.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Isim bos olamaz")
    @Size(min = 2, max = 60)
    private String name;

    @NotBlank(message = "E-posta bos olamaz")
    @Email(message = "Gecersiz e-posta")
    private String email;

    @NotBlank(message = "Sifre bos olamaz")
    @Size(min = 6, max = 64, message = "Sifre 6-64 karakter olmalidir")
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
