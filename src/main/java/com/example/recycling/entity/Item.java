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

// Create accessors
@Getter
@Setter
@Accessors(chain = true)
// Make it a mongodb document
@Document
public class Item {
    // POJO with following fields

    // use id as the id
    @Id
    private String id;
    private String condition;
    private String description;
    // wanted or offered
    private String status;
    // listUntilDate format as ISO when sending and receiving
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime listUntilDate;
    private String category;
    private List<Question> questions;
    private Boolean claimed;

    // Holds a reference to a user object
    @DBRef
    @Field("user")
    private User user;

    // When creating set default values
    private Item() {
        setQuestions(new LinkedList<>());
        setClaimed(false);
    }

    // Create an item with status set to offered
    public static Item offeredItem() {
        return new Item().setStatus(ConstantsService.OFFERED);
    }

    // Create an item with status set to wanted
    public static Item wantedItem() {
        return new Item().setStatus(ConstantsService.WANTED);
    }

    // Create a DTO with getter methods and a constructor which sets all parameters
    @Getter
    @AllArgsConstructor
    public static class ItemDTO {
        private String condition;
        private String description;
        // listUntilDate format as ISO when sending and receiving
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime listUntilDate;
        private String categories;
    }
}
