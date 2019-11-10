package com.example.recycling.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Response {
    private User sentBy;
    private String message;
}
