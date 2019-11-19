package com.example.recycling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

// Create accessor methods
@Getter
@Setter
@Accessors(chain = true)
public class EmailSettings {
    // POJO, add these fields
    @NonNull
    private Boolean verified;
    @NonNull
    private Boolean notifyQuestions;
    @NonNull
    private Boolean notifyResponses;
    @NonNull
    // Don't send this on requests
    @JsonIgnore
    private String verification;

    // When creating set default values
    public EmailSettings() {
        setVerified(false);
        setVerification(UUID.randomUUID().toString());
        setNotifyQuestions(true);
        setNotifyResponses(true);
    }


    public boolean isNotifiedOnQuestion() {
        // User is notified if verified and opted into notifications on questions
        return getVerified() && getNotifyQuestions();
    }

    public boolean isNotifiedOnResponse() {
        // User is notified if verified and opted into notifications on questions
        return getVerified() && getNotifyResponses();
    }

    // Create a DTO with getter methods and a constructor which sets all parameters
    @Getter
    @AllArgsConstructor
    public static class EmailSettingsDTO {
        private Boolean notifyQuestions;
        private Boolean notifyResponses;
    }

}
