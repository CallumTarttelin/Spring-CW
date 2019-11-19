package com.example.recycling.entity;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

// Create getters
@Getter
@Accessors(chain = true)
// Use all UserDetails fields so it can be used for security
public class RecyclingUser implements UserDetails {
    private final User user;

    // Create with a user
    public RecyclingUser(User user) {
        this.user = user;
    }

    @Override
    // Return user's authorities
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUser().getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    // Return user's password
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    // Return user's username
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}