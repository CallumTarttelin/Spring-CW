package com.example.recycling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Question {
    private User sentBy;
    private String message;
    private List<Response> response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        private String message;
    }
}
