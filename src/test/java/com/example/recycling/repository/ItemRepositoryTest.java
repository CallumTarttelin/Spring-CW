package com.example.recycling.repository;

import com.example.recycling.entity.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repo;

    private Item offered;
    private Item wanted;

    @BeforeEach
    void setUp() {
        offered = repo.save(Item.offeredItem().setDescription("Offered Item"));
        repo.save(Item.offeredItem().setDescription("Offered Item 2"));
        wanted = repo.save(Item.wantedItem().setDescription("Wanted Item"));
    }

    @AfterEach
    void tearDown() {
        repo.deleteAll();
    }

    @Test
    void findAllOffered() {
        List<Item> offeredItems = repo.findAllOffered();
        assertThat(offeredItems).hasSize(2);
        assertThat(offeredItems.stream().map(Item::getDescription)).contains(offered.getDescription());
    }

    @Test
    void findAllWanted() {
        List<Item> wantedItems = repo.findAllWanted();
        assertThat(wantedItems).hasSize(1);
        assertThat(wantedItems.stream().map(Item::getDescription)).contains(wanted.getDescription());
    }
}