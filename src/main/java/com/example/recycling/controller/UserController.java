package com.example.recycling.controller;

import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import com.example.recycling.service.EmailService;
import com.example.recycling.service.UserProvider;
import com.example.recycling.service.UserService;
import com.example.recycling.service.ConstantsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
// Precede all mappings with /api
@RequestMapping("/api/")
public class UserController {

    private final UserRepository repo;

    private final UserService userService;

    private final EmailService emailService;

    // Create controller with repository, userService, and emailService
    public UserController(UserRepository repo, UserService userService, EmailService emailService) {
        this.repo = repo;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Match get requests to /user
    @GetMapping("/user")
    // Allow only authenticated users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    public ResponseEntity<User> getLoggedIn() {
        // Return current user
        return ResponseEntity.ok(UserProvider.getUser());
    }

    // Match get requests to /user/*
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        // Return 200 and user if found otherwise 404
        return ResponseEntity.of(repo.findByUsernameIgnoreCase(id));
    }

    // Match post requests to /user
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> makeUser(User user) {
        // If the username is taken return 400
        if (repo.existsByUsernameIgnoreCase(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        // Encode password and add set them as authenticated
        user.setPassword(userService.passwordEncoder().encode(user.getPassword()));
        user.setAuthorities(new LinkedList<>(Collections.singleton(ConstantsService.AUTHENTICATED_USER)));
        // Save user
        User saved = repo.save(user);
        // Use emailService to send a verification email (won't send notifications till verified)
        emailService.sendVerificationEmail(saved);
        // Get location for current request, add /{id} to it, replace {id} with username, make it into a URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getUsername()).toUri();
        // Return 201 with link to user
        return ResponseEntity.created(location).build();
    }

    // Allow only authenticated users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Patch patch requests to /user
    @PatchMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> updateUser(User updated) {
        // Get logged in user
        User user = UserProvider.getUser();
        // Update user fields if present
        user.setPassword(updated.getPassword() != null ? userService.passwordEncoder().encode(updated.getPassword()) : user.getPassword());
        user.setPostcode(updated.getPostcode() != null ? updated.getPostcode() : user.getPostcode());
        if (updated.getEmail() != null && !updated.getEmail().equals(user.getEmail())) {
            // If email updated, make it not verified and send a new verification email
            user.setEmail(updated.getEmail());
            user.getEmailSettings().setVerified(false);
            user.getEmailSettings().setVerification(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user);
        }
        user.setEmail(updated.getEmail() != null ? updated.getEmail() : user.getEmail());
        user.setAddress(updated.getAddress() != null ? updated.getAddress() : user.getAddress());
        // Save user
        repo.save(user);
        // Return 204
        return ResponseEntity.noContent().build();
    }
}
