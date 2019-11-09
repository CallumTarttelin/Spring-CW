package com.example.recycling.controller;

import com.example.recycling.entity.OfferedItem;
import com.example.recycling.entity.User;
import com.example.recycling.repository.OfferedItemRepository;
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
class OfferedItemControllerTest {

    @Autowired
    private OfferedItemRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfferedItemController controller;

    private OfferedItem item;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        User user = new User()
                .setUsername("Arthur Dent")
                .setAddress("Earth")
                .setPostcode("postcode")
                .setPassword("Wow, it's a password!");
        userRepository.save(user);
        item = new OfferedItem()
                .setCondition("new")
                .setCategories(Arrays.asList("foo", "bar"))
                .setDescription("Itemy")
                .setListUntilDate(LocalDateTime.of(2019, 11, 9, 21, 55, 0))
                .setUser(user);
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
        mockMvc.perform(get("/api/offered/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", equalTo("Itemy")))
                .andExpect(jsonPath("listUntilDate", equalTo("2019-11-09T21:55:00Z")));
    }

    @Test
    @WithAnonymousUser
    void viewingNonexistentItem_404s() throws Exception {
        mockMvc.perform(get("/api/offered/NONEXISTANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_canViewAllItems() throws Exception {
        mockMvc.perform(get("/api/offered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotCreateItem() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/offered")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("condition=new&description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso%2Bitems"))
                .andExpect(status().isUnauthorized())).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void registeredUser_canCreateItem() throws Exception {
        String uri = mockMvc.perform(post("/api/offered")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("condition=new&description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso%2Bitems"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        assertThat(repo.existsById(id)).isTrue();
    }
}