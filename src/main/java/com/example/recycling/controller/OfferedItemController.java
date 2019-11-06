package com.example.recycling.controller;

import com.example.recycling.entity.OfferedItem;
import com.example.recycling.repository.OfferedItemRepository;
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
    @PostMapping("/offered")
    public ResponseEntity<Void> makeItem(OfferedItem.OfferedItemDTO offeredItemDTO) {
        OfferedItem item = new OfferedItem()
                .setCondition(offeredItemDTO.getCondition())
                .setDescription(offeredItemDTO.getDescription())
                .setListUntilDate(offeredItemDTO.getListUntilDate())
                .setCategories(offeredItemDTO.getCategories())
                .setUser(RecyclingUserProvider.getUser());
        OfferedItem saved = repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}