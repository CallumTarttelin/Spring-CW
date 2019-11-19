package com.example.recycling.entity;

import com.example.recycling.service.ConstantsService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.NonNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

// Create accessors
@Getter
@Setter
// Make class a mongodb document
@Document
@Accessors(chain = true)
public class User {
    // POJO fields

    // Set username as id which can't be null
    @Id @NonNull
    private String username;
    private String address;
    private String postcode;
    private String email;
    // Don't show the password on requests
    @JsonIgnore
    @JsonDeserialize
    private String password;
    // Don't show authorities on requests
    @JsonIgnore
    @NonNull
    private List<String> authorities;
    // Don't show email settings on requests
    @JsonIgnore
    @NonNull
    private EmailSettings emailSettings;

    // Create user with default values
    public User() {
        setAuthorities(new LinkedList<>(List.of(ConstantsService.AUTHENTICATED_USER)));
        setEmailSettings(new EmailSettings());
    }
}
