package com.example.recycling.controller;

import com.example.recycling.entity.Question;
import com.example.recycling.entity.OfferedItem;
import com.example.recycling.repository.OfferedItemRepository;
import com.example.recycling.service.UserProvider;
import com.example.recycling.service.RolesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/")
public class OfferedItemController {
    private final OfferedItemRepository repo;

    public OfferedItemController(OfferedItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/offered")
    public ResponseEntity<List<OfferedItem>> getItems() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/offered/{id}")
    public ResponseEntity<OfferedItem> getItem(@PathVariable String id) {
        return ResponseEntity.of(repo.findById(id));
    }

    @Secured(RolesService.AUTHENTICATED_USER)
    @PostMapping(value = "/offered", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> makeItem(OfferedItem.OfferedItemDTO offeredItemDTO) {
        OfferedItem item = new OfferedItem()
                .setCondition(offeredItemDTO.getCondition())
                .setDescription(offeredItemDTO.getDescription())
                .setListUntilDate(offeredItemDTO.getListUntilDate())
                .setCategories(offeredItemDTO.getCategories())
                .setUser(UserProvider.getUser())
                .setQuestions(new LinkedList<>());
        OfferedItem saved = repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Secured(RolesService.AUTHENTICATED_USER)
    @PostMapping(value = "/offered/{id}/question", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> askQuestion(@PathVariable String id, Question.QuestionDTO questionDTO) {
        Optional<OfferedItem> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        OfferedItem item = optionalItem.get();
        Question question = new Question()
                .setMessage(questionDTO.getMessage())
                .setResponse(new LinkedList<>())
                .setSentBy(UserProvider.getUser());
        item.getQuestions().add(question);
        repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/offered/{id}")
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

}