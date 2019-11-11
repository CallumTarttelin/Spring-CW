package com.example.recycling.controller;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.entity.Response;
import com.example.recycling.entity.User;
import com.example.recycling.repository.ItemRepository;
import com.example.recycling.repository.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ItemControllerTest {

    @MockBean
    private JavaMailSenderImpl mailSender;

    @Autowired
    private ItemRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemController controller;

    private Item item;
    private Question question;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        User user = userRepository
                .findByUsernameIgnoreCase("Arthur Dent")
                .orElse(userRepository.save(new User()
                        .setUsername("Arthur Dent")
                        .setAddress("Earth")
                        .setPostcode("postcode")
                        .setEmail("arthur.dent@example-domain.com")
                        .setPassword("Wow, it's a password!")
                ));
        user.getEmailSettings().setVerified(true);
        userRepository.save(user);
        item = Item.offeredItem()
                .setCondition("new")
                .setCategories(Arrays.asList("foo", "bar"))
                .setDescription("Itemy")
                .setListUntilDate(LocalDateTime.of(2019, 11, 9, 21, 55, 0))
                .setUser(user)
                .setStatus("Shouldn't Matter (overwrites offered)");
        question = new Question()
                .setMessage("Hello")
                .setResponses(new LinkedList<>(List.of(new Response().setMessage("Hi"))))
                .setSentBy(user);
        item.getQuestions().add(question);
        repo.save(item);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_canViewItem() throws Exception {
        mockMvc.perform(get("/api/item/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("condition", equalTo("new")))
                .andExpect(jsonPath("categories", contains("foo", "bar")))
                .andExpect(jsonPath("listUntilDate", equalTo("2019-11-09T21:55:00Z")));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotViewMissingItem() throws Exception {
        mockMvc.perform(get("/api/item/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotAskQuestion() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/item/" + item.getId() + "/question")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void withInvalidItem_cannotAskQuestion() throws Exception{
        mockMvc.perform(post("/api/item/invalid/question")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void registeredUser_canAskQuestion() throws Exception {
        when(mailSender.createMimeMessage()).thenCallRealMethod();
        String basePath = "/api/item/" + item.getId();
        String location = mockMvc.perform(post(basePath + "/question")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).endsWith(basePath);
        Item commented = repo.findById(item.getId()).orElseThrow();
        assertThat(commented.getQuestions()).hasSize(2);
        assertThat(commented.getQuestions().stream()
                .map(Question::getMessage)
                .collect(Collectors.toList())
        ).contains("Hello World");
        verify(mailSender).send(isA(MimeMessage.class));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotAnswer() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/item/" + item.getId() + "/question/" + question.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void withInvalidItem_cannotAnswer() throws Exception{
        mockMvc.perform(post("/api/item/invalid/question/" + item.getQuestions().get(0).getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void withInvalidQuestion_cannotAnswer() throws Exception{
        mockMvc.perform(post("/api/item/" + item.getId() + "/question/invalid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void registeredUser_canAnswerQuestion() throws Exception {
        when(mailSender.createMimeMessage()).thenCallRealMethod();

        String basePath = "/api/item/" + item.getId();
        String location = mockMvc.perform(post(basePath + "/question/" + question.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).endsWith(basePath);
        Item commented = repo.findById(item.getId()).orElseThrow();
        assertThat(commented.getQuestions().get(0).getResponses()).hasSize(2);
        assertThat(commented.getQuestions().stream()
                .flatMap(question -> question.getResponses().stream())
                .map(Response::getMessage)
                .collect(Collectors.toList())
        ).contains("Hello World");
        verify(mailSender).send(isA(MimeMessage.class));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotDelete() {
        assertThatThrownBy(() -> mockMvc.perform(delete("/api/item/" + item.getId()))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");

        assertThat(repo.existsById(item.getId())).isTrue();
    }

    @Test
    @WithMockUser
    void otherRegisteredUser_cannotDelete() throws Exception {
        mockMvc.perform(delete("/api/item/" + item.getId()))
                .andExpect(status().isForbidden());

        assertThat(repo.existsById(item.getId())).isTrue();
    }

    @Test
    @WithMockUser
    void registeredUser_cannotDeleteInvalidItem() throws Exception {
        mockMvc.perform(delete("/api/item/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void itemOwner_canDelete() throws Exception {
        mockMvc.perform(delete("/api/item/" + item.getId()))
                .andExpect(status().isNoContent());

        assertThat(repo.existsById(item.getId())).isFalse();
    }


    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotModify() {
        assertThatThrownBy(() -> mockMvc.perform(patch("/api/item/" + item.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=Hello+World"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");

        assertThat(repo.existsById(item.getId())).isTrue();
    }

    @Test
    @WithMockUser
    void otherRegisteredUser_cannotModify() throws Exception {
        mockMvc.perform(patch("/api/item/" + item.getId()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=Hello+World"))
                .andExpect(status().isForbidden());

        assertThat(repo.existsById(item.getId())).isTrue();
    }

    @Test
    @WithMockUser
    void registeredUser_cannotModifyInvalidItem() throws Exception {
        mockMvc.perform(patch("/api/item/invalid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void itemOwner_canModifyEverything() throws Exception {
        mockMvc.perform(patch("/api/item/" + item.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("condition=old&description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso+items"))
                .andExpect(status().isNoContent());

        Optional<Item> optionalUpdated = repo.findById(item.getId());
        assertThat(optionalUpdated).isPresent();
        Item updated = optionalUpdated.get();

        assertThat(updated.getStatus()).isEqualTo(item.getStatus());
        assertThat(updated.getCondition()).isEqualTo("old");
        assertThat(updated.getListUntilDate()).isEqualTo(LocalDateTime.of(2019, 11, 5, 21, 35, 48));
        assertThat(updated.getCategories()).containsExactly("items", "also items");
        assertThat(updated.getDescription()).isEqualTo("an item");
    }

    @Test
    @WithUserDetails("Arthur Dent")
    void itemOwner_canModifyNothing() throws Exception {
        String condition = item.getCondition();

        mockMvc.perform(patch("/api/item/" + item.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content(""))
                .andExpect(status().isNoContent());

        Optional<Item> optionalUpdated = repo.findById(item.getId());
        assertThat(optionalUpdated).isPresent();
        Item updated = optionalUpdated.get();

        assertThat(updated.getStatus()).isEqualTo(item.getStatus());
        assertThat(updated.getCondition()).isEqualTo(condition);
        assertThat(updated.getListUntilDate()).isEqualTo(item.getListUntilDate());
        assertThat(updated.getCategories()).isEqualTo(item.getCategories());
        assertThat(updated.getDescription()).isEqualTo(item.getDescription());
    }
}