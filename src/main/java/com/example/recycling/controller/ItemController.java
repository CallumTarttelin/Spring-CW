package com.example.recycling.controller;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.repository.ItemRepository;
import com.example.recycling.service.ConstantsService;
import com.example.recycling.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
