package com.example.recycling.controller;

import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import com.example.recycling.service.ConstantsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {


    @MockBean
    private JavaMailSenderImpl mailSender;
    @Autowired
    private UserRepository repo;
    @Autowired
    private UserController controller;

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
        arthur.getEmailSettings().setVerified(true);
        repo.save(arthur);

        zaphod = new User()
                .setUsername("Zaphod Beeblebrox")
                .setAddress("Heart of Gold")
                .setEmail("zaphod.beeblebrox@example-domain.com")
                .setPostcode("SPACE")
                .setPassword("foo")
                .setAuthorities(List.of(ConstantsService.AUTHENTICATED_USER, "Ex-Galactic_President"));
        zaphod.getEmailSettings().setVerified(true);
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
    void givenUserExists_thenShouldReturnUser_whenRetrieved() throws Exception {
        User retrieved = controller.getUser(arthur.getUsername()).getBody();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getAddress()).isEqualTo(arthur.getAddress());

        mockMvc.perform(get("/api/user/" + arthur.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", equalTo(arthur.getUsername())));
    }

    @Test
    void whenSendingValidUserData_aUserIsCreated() throws Exception {
        when(mailSender.createMimeMessage()).thenCallRealMethod();
        String uri = mockMvc.perform(post("/api/user")
                .content("username=Ford&address=My+House&postcode=My+Postcode&email=arthur.dent%40example-domain.com&password=password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        assertThat(repo.existsById(id)).isTrue();
        verify(mailSender).send(isA(MimeMessage.class));
    }

    @Test
    void duplicateUsername_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/user")
                .content("username=Arthur+Dent&address=My+House&postcode=My+Postcode&email=arthur.dent%40example-domain.com&password=password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenSendingJSON_isUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/user")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotPatchProfile() {
        assertThatThrownBy(() -> mockMvc.perform(patch("/api/user")
                .content("")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithUserDetails("Zaphod Beeblebrox")
    void registeredUser_keepsExisting() throws Exception {
        String originalPass = zaphod.getPassword();
        mockMvc.perform(patch("/api/user")
                .content("")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent());

        Optional<User> optionalUpdated = repo.findByUsernameIgnoreCase(zaphod.getUsername());
        assertThat(optionalUpdated).isPresent();
        User updated = optionalUpdated.get();
        assertThat(updated.getPassword()).isEqualTo(originalPass);
        assertThat(updated.getAddress()).isEqualTo(zaphod.getAddress());
    }

    @Test
    @WithUserDetails("Zaphod Beeblebrox")
    void registeredUser_canPatchProfile() throws Exception {
        when(mailSender.createMimeMessage()).thenCallRealMethod();
        String originalPass = zaphod.getPassword();
        mockMvc.perform(patch("/api/user")
                .content("password=Zaphod123&postcode=new+postcode&email=zaphod.beeblebrox%40example-domain.com&address=new+address")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent());

        Optional<User> optionalUpdated = repo.findByUsernameIgnoreCase(zaphod.getUsername());
        assertThat(optionalUpdated).isPresent();
        User updated = optionalUpdated.get();
        assertThat(updated.getPassword()).isNotEqualTo("Zaphod123");
        assertThat(updated.getPassword()).isNotEqualTo(originalPass);
        assertThat(updated.getEmail()).isEqualTo("zaphod.beeblebrox@example-domain.com");
        assertThat(updated.getEmailSettings().getVerified()).isFalse();
        verify(mailSender).send(isA(MimeMessage.class));
    }
}