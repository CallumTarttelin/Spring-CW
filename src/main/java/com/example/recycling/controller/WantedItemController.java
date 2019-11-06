package com.example.recycling.controller;

import com.example.recycling.entity.WantedItem;
import com.example.recycling.repository.WantedItemRepository;
import com.example.recycling.service.RecyclingUserProvider;
import com.example.recycling.service.RolesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    @PostMapping("/wanted")
    public ResponseEntity<Void> makeItem(WantedItem.WantedItemDTO wantedItemDTO) {
        WantedItem item = new WantedItem()
                .setDescription(wantedItemDTO.getDescription())
                .setListUntilDate(wantedItemDTO.getListUntilDate())
                .setCategories(wantedItemDTO.getCategories())
                .setUser(RecyclingUserProvider.getUser());
        WantedItem saved = repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}
