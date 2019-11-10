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

    public EmailSettingsController(UserRepository repo) {
        this.repo = repo;
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @GetMapping("/api/email-settings")
    public ResponseEntity<EmailSettings> getEmailSettings() {
        return ResponseEntity.ok(UserProvider.getUser().getEmailSettings());
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @PatchMapping(value = "/api/email-settings", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> updateEmailSettings(EmailSettings.EmailSettingsDTO update) {
        User user = UserProvider.getUser();
        EmailSettings settings = user.getEmailSettings();
        settings.setNotifyQuestions(update.getNotifyQuestions() != null ? update.getNotifyQuestions() : settings.getNotifyQuestions());
        settings.setNotifyResponses(update.getNotifyResponses() != null ? update.getNotifyResponses() : settings.getNotifyResponses());
        settings.setSubscribeToQuestions(update.getSubscribeToQuestions() != null ? update.getSubscribeToQuestions() : settings.getSubscribeToQuestions());
        repo.save(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify/{verification}")
    public ModelAndView verifyAccount(@PathVariable String verification) {
        User user = repo.findByEmailSettings_Verification(verification)
                .orElseThrow(NotFound::new);
        user.getEmailSettings().setVerified(true);
        repo.save(user);
        ModelAndView result = new ModelAndView();
        result.setViewName("verified");
        result.addObject("user", user.getUsername());
        result.addObject("email", user.getEmail());
        return result;
    }


}
