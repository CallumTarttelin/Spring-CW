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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WantedItemControllerTest {
    @Autowired
    private ItemRepository repo;

    @Autowired
    private WantedItemController controller;

    @Autowired
    private UserRepository userRepository;

    private Item item;
    private Question question;
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        User user = new User()
                .setUsername("Arthur Dent")
                .setAddress("Earth")
                .setPostcode("postcode")
                .setPassword("Wow, it's a password!");
        userRepository.save(user);
        item = Item.wantedItem()
                .setCategories(Arrays.asList("foo", "bar"))
                .setDescription("Itemy")
                .setListUntilDate(LocalDateTime.of(2019, 11, 9, 21, 55, 0))
                .setUser(user);
        question = new Question()
                .setMessage("Hello")
                .setResponses(new LinkedList<>());
        item.getQuestions().add(question);
        repo.save(item);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_canViewAnItemsDescription() throws Exception {
        mockMvc.perform(get("/api/wanted/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", equalTo("Itemy")));
    }

    @Test
    @WithAnonymousUser
    void viewingNonexistentItem_404s() throws Exception {
        mockMvc.perform(get("/api/wanted/NONEXISTANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_canViewAllItems() throws Exception {
        mockMvc.perform(get("/api/wanted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotCreateItem() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/wanted")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso%2Bitems"))
                .andExpect(status().isUnauthorized())).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void registeredUser_canCreateItem() throws Exception {
        String uri = mockMvc.perform(post("/api/wanted")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso%2Bitems"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        Optional<Item> optionalItem = repo.findById(id);
        assertThat(optionalItem).isPresent();
        Item item = optionalItem.get();
        assertThat(item.getQuestions()).isEmpty();
        assertThat(item.getDescription()).isEqualTo("an item");
        assertThat(item.getListUntilDate()).isEqualTo(LocalDateTime.of(2019, 11, 5, 21, 35, 48));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotComment() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/wanted")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void withInvalidItem_cannotComment() throws Exception{
        mockMvc.perform(post("/api/wanted/invalid/question")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void registeredUser_canComment() throws Exception {
        String basePath = "/api/wanted/" + item.getId();
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
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotAnswer() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/wanted/" + item.getId() + "/question/" + question.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void withInvalidItem_cannotAnswer() throws Exception{
        mockMvc.perform(post("/api/wanted/invalid/question/" + item.getQuestions().get(0).getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void withInvalidQuestion_cannotAnswer() throws Exception{
        mockMvc.perform(post("/api/wanted/" + item.getId() + "/question/invalid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void registeredUser_canAnswerQuestion() throws Exception {
        String basePath = "/api/wanted/" + item.getId();
        String location = mockMvc.perform(post(basePath + "/question/" + question.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("message=Hello+World"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        assertThat(location).endsWith(basePath);
        Item commented = repo.findById(item.getId()).orElseThrow();
        assertThat(commented.getQuestions().get(0).getResponses()).hasSize(1);
        assertThat(commented.getQuestions().stream()
                .flatMap(question -> question.getResponses().stream())
                .map(Response::getMessage)
                .collect(Collectors.toList())
        ).contains("Hello World");
    }

}