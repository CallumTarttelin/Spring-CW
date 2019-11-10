package com.example.recycling.controller;

import com.example.recycling.exception.NotFound;
import com.example.recycling.exception.UserForbidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UserForbidden.class)
    public ResponseEntity<Void> userForbidden() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
