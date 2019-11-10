package com.example.recycling.controller;

import com.example.recycling.entity.Question;
import com.example.recycling.entity.WantedItem;
import com.example.recycling.repository.WantedItemRepository;
import com.example.recycling.service.RolesService;
import com.example.recycling.service.UserProvider;
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
public class WantedItemController {

    private final WantedItemRepository repo;

    public WantedItemController(WantedItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/wanted")
    public ResponseEntity<List<WantedItem>> getItems() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/wanted/{id}")
    public ResponseEntity<WantedItem> getItem(@PathVariable String id) {
        return ResponseEntity.of(repo.findById(id));
    }

    @Secured(RolesService.AUTHENTICATED_USER)
    @PostMapping(value = "/wanted", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> makeItem(WantedItem.WantedItemDTO wantedItemDTO) {
        WantedItem item = new WantedItem()
                .setDescription(wantedItemDTO.getDescription())
                .setListUntilDate(wantedItemDTO.getListUntilDate())
                .setCategories(wantedItemDTO.getCategories())
                .setUser(UserProvider.getUser());
        WantedItem saved = repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Secured(RolesService.AUTHENTICATED_USER)
    @PostMapping(value = "/wanted/{id}/question", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> askQuestion(@PathVariable String id, Question.QuestionDTO questionDTO) {
        Optional<WantedItem> wantedItem = repo.findById(id);
        if (wantedItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        WantedItem item = wantedItem.get();
        Question question = new Question()
                .setMessage(questionDTO.getMessage())
                .setResponse(new LinkedList<>())
                .setSentBy(UserProvider.getUser());
        item.getQuestions().add(question);
        repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/wanted/{id}")
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

}
