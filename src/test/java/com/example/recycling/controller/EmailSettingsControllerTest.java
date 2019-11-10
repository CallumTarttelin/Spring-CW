package com.example.recycling.controller;

import com.example.recycling.entity.User;
import com.example.recycling.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class EmailSettingsControllerTest {

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailSettingsController controller;

    @Autowired
    private ExceptionController exceptionController;

    private MockMvc mockMvc;
    private User verified;
    private User unverified;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(exceptionController).build();
        resetDb();
    }

    @AfterEach
    void tearDown() {
        resetDb();
    }

    private void resetDb() {
        repo.deleteAll();
        verified = new User().setUsername("Arthur Dent");
        verified.getEmailSettings().setVerified(true);
        verified = repo.save(verified);
        unverified = repo.save(new User().setUsername("Ford Prefect"));
    }

    @Test
    void verifyingValidAccount_verifies() throws Exception {
        assertThat(unverified.getEmailSettings().getVerified()).isFalse();
        mockMvc.perform(get("/verify/" + unverified.getEmailSettings().getVerification())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("verified"));
        User retrieved = repo.findById(unverified.getUsername()).orElseThrow(AssertionError::new);
        assertThat(retrieved.getEmailSettings().getVerified()).isTrue();
    }

    @Test
    void verifyingInvalidAccount_isNotFound() throws Exception {
        assertThat(unverified.getEmailSettings().getVerified()).isFalse();
        mockMvc.perform(get("/verify/invalid"))
                .andExpect(status().isNotFound());
        assertThat(unverified.getEmailSettings().getVerified()).isFalse();
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotGetEmailSettings() {
        assertThatThrownBy(() ->
            mockMvc.perform(get("/api/email-settings"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void registeredVerifiedUser_canGetEmailSettings() throws Exception {
        mockMvc.perform(get("/api/email-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("verified", equalTo(true)));
    }

    @Test
    @WithUserDetails("Ford Prefect")
    void registeredUser_canGetEmailSettings() throws Exception {
        mockMvc.perform(get("/api/email-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("verified", equalTo(false)));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotUpdateEmailSettings() {
        assertThatThrownBy(() ->
                mockMvc.perform(patch("/api/email-settings")
                        .content("notifyQuestions=false&notifyResponses=false&subscribeToQuestions=true")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .characterEncoding("UTF-8"))
                        .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void registeredUser_canUpdateEmailSettings() throws Exception {
        mockMvc.perform(patch("/api/email-settings")
                .content("notifyQuestions=false&notifyResponses=false&subscribeToQuestions=true")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent());
        User updated = repo.findByUsernameIgnoreCase(verified.getUsername()).orElseThrow(AssertionError::new);
        assertThat(updated.getEmailSettings().isNotifiedOnQuestion()).isFalse();
        assertThat(updated.getEmailSettings().isNotifiedOnResponse()).isFalse();
        assertThat(updated.getEmailSettings().isSubscribedToQuestion()).isTrue();
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void registeredUser_canLeaveEmailSettings() throws Exception {
        mockMvc.perform(patch("/api/email-settings")
                .content("")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent());
        User updated = repo.findByUsernameIgnoreCase(verified.getUsername()).orElseThrow(AssertionError::new);
        assertThat(updated.getEmailSettings().isNotifiedOnQuestion()).isTrue();
        assertThat(updated.getEmailSettings().isNotifiedOnResponse()).isTrue();
        assertThat(updated.getEmailSettings().isSubscribedToQuestion()).isFalse();
    }

}