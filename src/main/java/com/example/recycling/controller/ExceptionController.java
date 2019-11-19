package com.example.recycling.controller;

import com.example.recycling.exception.NotFound;
import com.example.recycling.exception.UserForbidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    // Match any thrown NotFound exception
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<Void> notFound() {
        // Return 404
        return ResponseEntity.notFound().build();
    }

    // Match any UserForbidden exception
    @ExceptionHandler(UserForbidden.class)
    public ResponseEntity<Void> userForbidden() {
        // Return 403
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
