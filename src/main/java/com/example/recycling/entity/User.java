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

@Getter
@Setter
@Document
@Accessors(chain = true)
public class User {
    @Id @NonNull
    private String username;
    private String address;
    private String postcode;
    private String email;
    @JsonIgnore
    @JsonDeserialize
    private String password;
    @JsonIgnore
    @NonNull
    private List<String> authorities;
    @JsonIgnore
    @NonNull
    private EmailSettings emailSettings;

    public User() {
        setAuthorities(new LinkedList<>(List.of(ConstantsService.AUTHENTICATED_USER)));
        setEmailSettings(new EmailSettings());
    }
}
