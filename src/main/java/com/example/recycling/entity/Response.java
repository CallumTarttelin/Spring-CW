package com.example.recycling.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Accessors(chain = true)
public class Response {
    private String message;
    @DBRef
    @Field("sentBy")
    private User sentBy;
}
