package com.example.recycling.controller;

import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import com.example.recycling.service.RolesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    UserRepository repo;
    @Autowired
    UserController controller;

    private User arthur;
    private User zaphod;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        clearAndRebuild();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        clearAndRebuild();
    }

    private void clearAndRebuild() {
        repo.deleteAll();
        arthur = new User()
                .setUsername("Arthur Dent")
                .setAddress("Earth")
                .setPostcode("postcode")
                .setPassword("Wow, it's a password!");
        repo.save(arthur);

        zaphod = new User()
                .setUsername("Zaphod Beeblebrox")
                .setAddress("Heart of Gold")
                .setEmail("zaphod@beeblebrox.com")
                .setPostcode("SPACE")
                .setPassword("foo")
                .setAuthorities(List.of(RolesService.AUTHENTICATED_USER, "Ex-Galactic_President"));
        repo.save(zaphod);
    }

    @Test
    void whenGettingUsers_AppReturnsAllUsers() throws Exception {
        List<String> userNames = controller.getUserNames().getBody();
        assertThat(userNames).isNotNull();
        assertThat(userNames.contains(arthur.getUsername())).isTrue();
        assertThat(userNames.size()).isEqualTo(2);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", equalTo(arthur.getUsername())));
    }

    @Test
    void givenUserExists_whenRetrieved_shouldReturnUser() throws Exception {
        User retrieved = controller.getUser(arthur.getUsername()).getBody();
        assertThat(retrieved).isEqualTo(arthur);

        mockMvc.perform(get("/api/user/" + arthur.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", equalTo(arthur.getUsername())));
    }

    @Test
    void whenSendingValidUserData_aUserIsCreated() throws Exception {
        User newUser = new User().setUsername("Ford Prefect").setPassword("password");
        controller.makeUser(newUser);
        assertThat(repo.existsById(newUser.getUsername())).isTrue();

        String uri = mockMvc.perform(post("/api/user")
                .content("username=Saskcow&address=My+House&postcode=My+Postcode&email=my%40email.com&password=password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        assertThat(repo.existsById(id)).isTrue();
    }

    @Test
    void whenSendingJSON_isUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/user")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithUserDetails("Zaphod Beeblebrox")
    // TODO see if I can make sure this always has user
    void registeredUser_canPatchProfile() throws Exception {
        String originalPass = zaphod.getPassword();
        mockMvc.perform(patch("/api/user")
                .content("{\"password\": \"bar\", \"address\": \"foo\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<User> optionalUpdated = repo.findByUsernameIgnoreCase(zaphod.getUsername());
        assertThat(optionalUpdated).isPresent();
        User updated = optionalUpdated.get();
        assertThat(updated.getPassword()).isNotEqualTo("bar");
        assertThat(updated.getPassword()).isNotEqualTo(originalPass);
        assertThat(updated.getAddress()).isEqualTo("foo");
    }
}