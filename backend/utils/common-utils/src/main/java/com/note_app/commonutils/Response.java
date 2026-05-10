package com.note_app.commonutils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response<T> {
    private T data;
    private int httpCode;

    public Response(T data, int httpCode) {
        this.data = data;
        this.httpCode = httpCode;
    }

    public ResponseEntity<T> toResponseEntity() {
        return new ResponseEntity<>(data, HttpStatus.valueOf(this.httpCode));
    }
}
