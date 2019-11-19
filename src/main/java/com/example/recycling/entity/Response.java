package com.example.recycling.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

// Create accessors
@Getter
@Setter
@Accessors(chain = true)
public class Response {
    // POJO fields

    private String message;
    // Reference to user
    @DBRef
    @Field("sentBy")
    private User sentBy;
}
