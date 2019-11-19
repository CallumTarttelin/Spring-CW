package com.example.recycling.entity;

import com.example.recycling.service.ConstantsService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Document
public class Item {
    @Id
    private String id;
    private String condition;
    private String description;
    // wanted or offered
    private String status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime listUntilDate;
    private String category;
    private List<Question> questions;
    private Boolean claimed;

    @DBRef
    @Field("user")
    private User user;

    private Item() {
        setQuestions(new LinkedList<>());
        setClaimed(false);
    }

    public static Item offeredItem() {
        return new Item().setStatus(ConstantsService.OFFERED);
    }

    public static Item wantedItem() {
        return new Item().setStatus(ConstantsService.WANTED);
    }

    @Getter
    @AllArgsConstructor
    public static class ItemDTO {
        private String condition;
        private String description;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime listUntilDate;
        private String categories;
    }
}
