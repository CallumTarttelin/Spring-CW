package com.example.recycling.repository;

import com.example.recycling.entity.Item;
import com.example.recycling.service.ConstantsService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {

    // findAllOffered returns response of the query
    @Query("{status: " + ConstantsService.OFFERED + "}")
    List<Item> findAllOffered();

    // findAllWanted returns response of the query
    @Query("{status: " + ConstantsService.WANTED + "}")
    List<Item> findAllWanted();

    // How it could get all for user if required
    // List<Item> findAllByUser(User user);
}
