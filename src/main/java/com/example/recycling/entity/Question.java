package com.example.recycling.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class Question {
    private String id;
    private User sentBy;
    private String message;
    private List<Response> responses;

    public Question() {
        this.setId(UUID.randomUUID().toString());
    }

    @Getter
    @AllArgsConstructor
    public static class QuestionDTO {
        private String message;
    }
}
