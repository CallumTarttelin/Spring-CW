package com.example.recycling.repository;

import com.example.recycling.entity.OfferedItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfferedItemRepository extends MongoRepository<OfferedItem, String> {
}
