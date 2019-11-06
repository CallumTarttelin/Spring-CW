package com.example.recycling.controller;

import com.example.recycling.entity.WantedItem;
import com.example.recycling.repository.WantedItemRepository;
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
class WantedItemControllerTest {
    @Autowired
    private WantedItemRepository repo;

    @Autowired
    private WantedItemController controller;

    private WantedItem item;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        item = new WantedItem().setCategories(Arrays.asList("foo", "bar")).setDescription("Itemy");
        repo.save(item);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
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
                .content("description=an+item&listUntilDate=2019-11-05+21%3A35%3A48&categories=items%2Calso%2Bitems"))
                .andExpect(status().isUnauthorized())).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void registeredUser_canCreateItem() throws Exception {
        String uri = mockMvc.perform(post("/api/wanted")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("description=an+item&listUntilDate=2019-11-05+21%3A35%3A48&categories=items%2Calso%2Bitems"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        assertThat(repo.existsById(id)).isTrue();
    }

}