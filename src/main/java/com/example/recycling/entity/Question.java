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

@Getter
@Setter
@Accessors(chain = true)
public class Question {
    private String id;
    private String message;
    private List<Response> responses;
    @DBRef
    @Field("sentBy")
    private User sentBy;

    public Question() {
        this.setId(UUID.randomUUID().toString());
        this.setResponses(new LinkedList<>());
    }

    @Getter
    @AllArgsConstructor
    public static class QuestionDTO {
        @NonNull
        private String message;
    }
}
