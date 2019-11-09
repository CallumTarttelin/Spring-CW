package com.example.recycling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @NonNull
    private String username;
    private String address;
    private String postcode;
    private String email;
    @JsonIgnore
    @JsonDeserialize
    private String password;
    @JsonIgnore
    private List<String> authorities;
}
