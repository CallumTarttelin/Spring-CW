package com.example.recycling.service;

import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository repo;

    @Autowired
    UserService service;

    @AfterEach
    void tearDown() {
        repo.deleteAll();
    }

    @Test
    void loadUserByUsername_forInvalidUser() {
        assertThatThrownBy(() -> service.loadUserByUsername("Mr Prosser")).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_successful() {
        String username = "Prostetnic Vogon Jeltz";
        repo.save(new User().setUsername(username)
                .setEmail("jeltz.vogon@callumtarttelin.com")
                .setPassword("pass")
                .setAuthorities(List.of("VOGON_COMMANDER"))
        );
        UserDetails found = service.loadUserByUsername(username);
        assertThat(found.getUsername()).isEqualTo(username);
        assertThat(found.getPassword()).isEqualTo("pass");
        assertThat(found.getAuthorities().stream().map(GrantedAuthority::getAuthority)).contains("VOGON_COMMANDER");
        assertThat(found.isAccountNonExpired()).isTrue();
        assertThat(found.isAccountNonLocked()).isTrue();
        assertThat(found.isEnabled()).isTrue();
        assertThat(found.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void passwordEncoder() {
        assertThat(service.passwordEncoder()).isNotNull();
    }
}