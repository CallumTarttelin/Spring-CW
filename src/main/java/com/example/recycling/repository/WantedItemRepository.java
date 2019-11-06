package com.example.recycling.repository;

import com.example.recycling.entity.WantedItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WantedItemRepository extends MongoRepository<WantedItem, String> {
}
