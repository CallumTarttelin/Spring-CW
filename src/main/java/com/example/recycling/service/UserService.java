package com.example.recycling.service;

import com.example.recycling.entity.RecyclingUser;
import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new RecyclingUser(user.get());
    }

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(64, 128, 4, 1<<16, 5);
    }
}
