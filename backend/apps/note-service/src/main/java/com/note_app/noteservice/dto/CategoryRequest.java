package com.note_app.noteservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Kategori adi bos olamaz")
    @Size(max = 100, message = "Kategori adi en fazla 100 karakter olabilir")
    private String name;

    @Size(max = 20)
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
