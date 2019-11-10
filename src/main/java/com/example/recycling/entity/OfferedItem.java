package com.example.recycling.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
@Document
public class OfferedItem {
    @Id
    private String id;
    private String condition;
    private String description;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime listUntilDate;
    private List<String> categories;
    private List<Question> questions;

    public OfferedItem() {
        this.questions = new LinkedList<>();
    }

    @DBRef
    @Field("user")
    private User user;

    @Data
    @NoArgsConstructor
    public static class OfferedItemDTO {
        private String condition;
        private String description;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime listUntilDate;
        private List<String> categories;
    }
}
