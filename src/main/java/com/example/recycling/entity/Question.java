package com.example.recycling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class Question {
    private String id;
    private User sentBy;
    private String message;
    private List<Response> responses;

    public Question() {
        this.id = UUID.randomUUID().toString();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        private String message;
    }
}
