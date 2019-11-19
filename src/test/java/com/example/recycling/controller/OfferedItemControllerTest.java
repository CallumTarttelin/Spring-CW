package com.example.recycling.controller;

import com.example.recycling.entity.Item;
import com.example.recycling.repository.ItemRepository;
import com.example.recycling.service.ConstantsService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OfferedItemControllerTest {

    @Autowired
    private ItemRepository repo;

    @Autowired
    private OfferedItemController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        repo.save(Item.offeredItem());
        repo.save(Item.offeredItem());
        repo.save(Item.wantedItem());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_canViewAllItems() throws Exception {
        mockMvc.perform(get("/api/offered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithAnonymousUser
    void unregisteredUser_cannotCreateItem() {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/offered")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("condition=new&description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso+items"))
                .andExpect(status().isUnauthorized())
        ).isInstanceOf(NestedServletException.class).hasMessageContaining("Access is denied");
    }

    @Test
    @WithMockUser
    void registeredUser_canCreateItem() throws Exception {
        String uri = mockMvc.perform(post("/api/offered")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .characterEncoding("UTF-8")
                .content("condition=new&description=an+item&listUntilDate=2019-11-05T21%3A35%3A48Z&categories=items%2Calso+items"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        assertThat(uri).isNotNull();
        String id = uri.split("/")[uri.split("/").length - 1];

        Optional<Item> optionalItem = repo.findById(id);
        assertThat(optionalItem).isPresent();
        Item item = optionalItem.get();
        assertThat(item.getQuestions()).isEmpty();
        assertThat(item.getCondition()).isEqualTo("new");
        assertThat(item.getStatus()).isEqualTo(ConstantsService.OFFERED);
        assertThat(item.getCategory()).isEqualTo("items,also items");
    }

}