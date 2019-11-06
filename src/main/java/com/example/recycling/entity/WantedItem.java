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
public class WantedItem {
    @Id
    private String id;
    private String description;
    private Timestamp listUntilDate;
    private List<String> categories;

    @DBRef
    @Field("user")
    private User user;

    @Data
    @NoArgsConstructor
    public static class WantedItemDTO {
        private String description;
        private Timestamp listUntilDate;
        private List<String> categories;
    }
}
