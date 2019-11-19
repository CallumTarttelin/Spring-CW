package com.example.recycling.entity;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

// Create accessors
@Getter
@Setter
@Accessors(chain = true)
public class Question {
    // POJO things, has these fields
    private String id;
    private String message;
    private List<Response> responses;
    // Has reference to user object
    @DBRef
    @Field("sentBy")
    private User sentBy;

    // Create with default values
    public Question() {
        this.setId(UUID.randomUUID().toString());
        this.setResponses(new LinkedList<>());
    }

    // Create a DTO with getter methods and a constructor which sets all parameters
    @Getter
    @AllArgsConstructor
    public static class QuestionDTO {
        @NonNull
        private String message;
    }
}
