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
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ItemController {

    private final ItemRepository repo;
    private final ItemService service;

    public ItemController(ItemRepository repo, ItemService service) {
        this.repo = repo;
        this.service = service;
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Item> getItem(@PathVariable String id) {
        return ResponseEntity.of(repo.findById(id));
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @PostMapping(value = "/item/{id}/question", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> askQuestion(@PathVariable String id, Question.QuestionDTO questionDTO) {
        return service.askQuestion(id, questionDTO);
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @PostMapping(value = "/item/{itemId}/question/{questionId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> answerQuestion(@PathVariable String itemId, @PathVariable String questionId, Question.QuestionDTO questionDTO) {
        return service.answerQuestion(itemId, questionId, questionDTO);
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        Optional<Item> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        if (! item.getUser().equals(UserProvider.getUser())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Secured(ConstantsService.AUTHENTICATED_USER)
    @PatchMapping("/item/{id}")
    public ResponseEntity<Void> updateItem(@PathVariable String id, Item.ItemDTO updated) {
        Optional<Item> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        if (! item.getUser().equals(UserProvider.getUser())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        item.setCondition(updated.getCondition() != null ? updated.getCondition() : item.getCondition());
        item.setDescription(updated.getDescription() != null ? updated.getDescription() : item.getDescription());
        item.setListUntilDate(updated.getListUntilDate() != null ? updated.getListUntilDate() : item.getListUntilDate());
        item.setCategories(updated.getCategories() != null ? List.of(updated.getCategories().split(",")) : item.getCategories());
        repo.save(item);
        return ResponseEntity.noContent().build();
    }

}
