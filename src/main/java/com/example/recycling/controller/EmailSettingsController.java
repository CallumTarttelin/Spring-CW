package com.example.recycling.controller;

import com.example.recycling.entity.EmailSettings;
import com.example.recycling.entity.User;
import com.example.recycling.exception.NotFound;
import com.example.recycling.repository.UserRepository;
import com.example.recycling.service.ConstantsService;
import com.example.recycling.service.UserProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EmailSettingsController {

    private final UserRepository repo;

    // Create and set the repo field with a UserRepository
    public EmailSettingsController(UserRepository repo) {
        this.repo = repo;
    }

    // Require user to be authenticated
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match get requests to /api/email-settings
    @GetMapping("/api/email-settings")
    public ResponseEntity<EmailSettings> getEmailSettings() {
        // Return 200 with active users email settings
        return ResponseEntity.ok(UserProvider.getUser().getEmailSettings());
    }

    // Require user to be authenticated
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match post requests to /api/email-settings accepting form urlencoded
    @PatchMapping(value = "/api/email-settings", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> updateEmailSettings(EmailSettings.EmailSettingsDTO update) {
        // Get a user and their email settings
        User user = UserProvider.getUser();
        EmailSettings settings = user.getEmailSettings();
        // If present on update, change the fields to new values
        settings.setNotifyQuestions(update.getNotifyQuestions() != null ? update.getNotifyQuestions() : settings.getNotifyQuestions());
        settings.setNotifyResponses(update.getNotifyResponses() != null ? update.getNotifyResponses() : settings.getNotifyResponses());
        // Save changes and return 204
        repo.save(user);
        return ResponseEntity.noContent().build();
    }

    // Match get requests to /verify/*
    @GetMapping("/verify/{verification}")
    public ModelAndView verifyAccount(@PathVariable String verification) {
        // Find the user, otherwise throw NotFound (handled elsewhere makes it return 404)
        User user = repo.findByEmailSettings_Verification(verification)
                .orElseThrow(NotFound::new);
        // Get the user email settings, verify their email, and save that
        user.getEmailSettings().setVerified(true);
        repo.save(user);
        // Create a new view
        ModelAndView result = new ModelAndView();
        // Base view off of verified.mustache
        result.setViewName("verified");
        // Fill template in with username and email then return the page
        result.addObject("user", user.getUsername());
        result.addObject("email", user.getEmail());
        return result;
    }


}
