package com.example.recycling.controller;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.repository.ItemRepository;
import com.example.recycling.service.ConstantsService;
import com.example.recycling.service.ItemService;
import com.example.recycling.service.UserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
// Precede all mappings with /api
@RequestMapping("/api")
public class ItemController {

    private final ItemRepository repo;
    private final ItemService service;

    // Create controller setting repo and service
    public ItemController(ItemRepository repo, ItemService service) {
        this.repo = repo;
        this.service = service;
    }

    // Match all get requests to /item/*
    @GetMapping("/item/{id}")
    public ResponseEntity<Item> getItem(@PathVariable String id) {
        // Return 200 if present, or 404 if not found
        return ResponseEntity.of(repo.findById(id));
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match post requests to /item/*/question accepting application/x-www-form-urlencoded
    @PostMapping(value = "/item/{id}/question", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> askQuestion(@PathVariable String id, Question.QuestionDTO questionDTO) {
        // Call service and the response
        return service.askQuestion(id, questionDTO);
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match all post requests to /item/*/question/*
    @PostMapping(value = "/item/{itemId}/question/{questionId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> answerQuestion(@PathVariable String itemId, @PathVariable String questionId, Question.QuestionDTO questionDTO) {
        // Call service and the response
        return service.answerQuestion(itemId, questionId, questionDTO);
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match all delete requests to /item/*
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        // Find item in repository, if not present return a 404
        Optional<Item> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        // If the user does not match the item owner return 403
        if (! item.getUser().getUsername().equals(UserProvider.getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Delete item and return 204
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match app patch requests to /item/*
    @PatchMapping("/item/{id}")
    public ResponseEntity<Void> updateItem(@PathVariable String id, Item.ItemDTO updated) {
        // Find user by id and make it null if empty
        Item item = repo.findById(id).orElse(null);
        // Return ResponseEntity if returned from requireOwner otherwise return the orElseGet
        return requireOwner(item).orElseGet(() -> {
            // Ensure item is not null (shouldn't be)
            Objects.requireNonNull(item);
            // Update fields if in updated the fields are not null and save
            item.setCondition(updated.getCondition() != null ? updated.getCondition() : item.getCondition());
            item.setDescription(updated.getDescription() != null ? updated.getDescription() : item.getDescription());
            item.setListUntilDate(updated.getListUntilDate() != null ? updated.getListUntilDate() : item.getListUntilDate());
            item.setCategory(updated.getCategories() != null ? updated.getCategories() : item.getCategory());
            repo.save(item);
            // Return 204
            return ResponseEntity.noContent().build();
        });
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match Post requests to /item/*/claim
    @PostMapping("/item/{id}/claim")
    public ResponseEntity<Void> claimItem(@PathVariable String id) {
        // Find user by id and make it null if empty
        Item item = repo.findById(id).orElse(null);
        // Return ResponseEntity if returned from requireOwner otherwise return the orElseGet
        return requireOwner(item).orElseGet(() -> {
            // Ensure item is not null (never should be)
            Objects.requireNonNull(item);
            // Set claimed to true and save
            item.setClaimed(true);
            repo.save(item);
            // Return 204
            return ResponseEntity.noContent().build();
        });
    }

    // Only allow secured users
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match Post requests to /item/*/unclaim
    @PostMapping("/item/{id}/unclaim")
    public ResponseEntity<Void> unclaimItem(@PathVariable String id) {
        // Find user by id and make it null if empty
        Item item = repo.findById(id).orElse(null);
        // Return ResponseEntity if returned from requireOwner otherwise return the orElseGet
        return requireOwner(item).orElseGet(() -> {
            // Ensure item definitely isn't null (it never should be)
            Objects.requireNonNull(item);
            // Set claimed to false and save
            item.setClaimed(false);
            repo.save(item);
            // Return 204
            return ResponseEntity.noContent().build();
        });
    }

    private Optional<ResponseEntity<Void>> requireOwner(Item item) {
        // Find item in repository, if not present return a 404
        if (item == null) {
            return Optional.of(ResponseEntity.notFound().build());
        }
        // If the user does not match the item owner return 403
        if (! item.getUser().getUsername().equals(UserProvider.getUsername())) {
            return Optional.of(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
        // Otherwise return empty
        return Optional.empty();
    }

}
