package com.example.recycling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmailSettings {
    @NonNull
    private Boolean verified;
    @NonNull
    private Boolean notifyQuestions;
    @NonNull
    private Boolean notifyResponses;
    @NonNull
    @JsonIgnore
    private String verification;

    public EmailSettings() {
        setVerified(false);
        setVerification(UUID.randomUUID().toString());
        setNotifyQuestions(true);
        setNotifyResponses(true);
    }

    public boolean isNotifiedOnQuestion() {
        return getVerified() && getNotifyQuestions();
    }

    public boolean isNotifiedOnResponse() {
        return getVerified() && getNotifyResponses();
    }

    @Getter
    @AllArgsConstructor
    public static class EmailSettingsDTO {
        private Boolean notifyQuestions;
        private Boolean notifyResponses;
    }

}
