package com.example.recycling.repository;

import com.example.recycling.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository repo;

    @Test
    void testInsertionAndRetrieval() {
        User user = new User().setUsername("foo");
        repo.save(user);

        Optional<User> found = repo.findByUsernameIgnoreCase(user.getUsername());

        assertThat(found).isPresent();
        User foundUser = found.get();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }
}