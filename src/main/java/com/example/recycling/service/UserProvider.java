package com.example.recycling.service;

import com.example.recycling.entity.RecyclingUser;
import com.example.recycling.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserProvider {
    private UserProvider() {}

    private static Optional<RecyclingUser> getRecyclingUser() {
        try {
            return Optional.of((RecyclingUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (NullPointerException|ClassCastException e) {
            return Optional.empty();
        }
    }

    private static Optional<User> getOptionalUser() {
        return getRecyclingUser().map(RecyclingUser::getUser);
    }

    public static User getUser() {
        return getOptionalUser().orElse(null);
    }

    public static String getUsername() {
        return getRecyclingUser().map(RecyclingUser::getUsername).orElse(null);
    }

}
