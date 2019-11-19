package com.example.recycling.repository;

import com.example.recycling.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Return optional of user found by username
    Optional<User> findByUsernameIgnoreCase(String username);

    // Check if user exists by username
    boolean existsByUsernameIgnoreCase(String username);

    // Find user by email verification
    Optional<User> findByEmailSettings_Verification(String verification);
}
