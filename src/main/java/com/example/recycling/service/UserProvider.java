package com.example.recycling.service;

import com.example.recycling.entity.RecyclingUser;
import com.example.recycling.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserProvider {
    // Don't allow instances of this object
    private UserProvider() {}

    // Return optional of the RecyclingUser from context
    private static Optional<RecyclingUser> getRecyclingUser() {
        try {
            return Optional.of((RecyclingUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (NullPointerException|ClassCastException e) {
            return Optional.empty();
        }
    }

    // Get optional of the User
    private static Optional<User> getOptionalUser() {
        return getRecyclingUser().map(RecyclingUser::getUser);
    }

    // Get user with null if not present
    public static User getUser() {
        return getOptionalUser().orElse(null);
    }

    // Get username with null if not present
    public static String getUsername() {
        return getRecyclingUser().map(RecyclingUser::getUsername).orElse(null);
    }

}
