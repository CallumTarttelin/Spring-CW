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
@RequestMapping("/api/")
public class UserController {

    private final UserRepository repo;

    private final UserService userService;

    private final EmailService emailService;

    public UserController(UserRepository repo, UserService userService, EmailService emailService) {
        this.repo = repo;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<String>> getUserNames() {
        List<String> names = repo.findAll().stream().map(User::getUsername).collect(Collectors.toList());
        return ResponseEntity.ok(names);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.of(repo.findByUsernameIgnoreCase(id));
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> makeUser(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        if (repo.existsByUsernameIgnoreCase(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(userService.passwordEncoder().encode(user.getPassword()));
        user.setAuthorities(new LinkedList<>(Collections.singleton(ConstantsService.AUTHENTICATED_USER)));
        User saved = repo.save(user);
        emailService.sendVerificationEmail(saved);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getUsername()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @PatchMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> updateUser(User updated) {
        User user = UserProvider.getUser();
        user.setPassword(updated.getPassword() != null ? userService.passwordEncoder().encode(updated.getPassword()) : user.getPassword());
        user.setPostcode(updated.getPostcode() != null ? updated.getPostcode() : user.getPostcode());
        if (updated.getEmail() != null) {
            user.setEmail(updated.getEmail());
            user.getEmailSettings().setVerified(false);
            user.getEmailSettings().setVerification(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user);
        }
        user.setEmail(updated.getEmail() != null ? updated.getEmail() : user.getEmail());
        user.setAddress(updated.getAddress() != null ? updated.getAddress() : user.getAddress());
        repo.save(user);
        return ResponseEntity.noContent().build();
    }
}
