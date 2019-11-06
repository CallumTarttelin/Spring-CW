package com.example.recycling.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
class Message {
    private User sentBy;
    private String message;
    private List<Message> response;
}
