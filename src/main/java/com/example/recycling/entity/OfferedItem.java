package com.example.recycling.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.List;

@Data
@Accessors(chain = true)
@Document
public class OfferedItem {
    @Id
    private String id;
    private String condition;
    private String description;
    private Timestamp listUntilDate;
    // TODO get timestamps retrieved properly
    private List<String> categories;
    private List<Message> messages;

    @DBRef
    @Field("user")
    private User user;

    @Data
    @NoArgsConstructor
    public static class OfferedItemDTO {
        private String condition;
        private String description;
        private Timestamp listUntilDate;
        private List<String> categories;
    }
}
