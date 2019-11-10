package com.example.recycling.repository;

import com.example.recycling.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}
