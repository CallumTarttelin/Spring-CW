package com.example.recycling.controller;

import com.example.recycling.entity.Item;
import com.example.recycling.repository.ItemRepository;
import com.example.recycling.service.ConstantsService;
import com.example.recycling.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
// Precede all mappings with /api
@RequestMapping("/api/")
public class OfferedItemController {
    private final ItemRepository repo;
    private final ItemService service;

    // Initialize controller with repository and service
    public OfferedItemController(ItemRepository repo, ItemService service) {
        this.repo = repo;
        this.service = service;
    }

    // Match get mappings to /offered
    @GetMapping("/offered")
    public ResponseEntity<List<Item>> getItems() {
        // returns 200 with all offered items
        return ResponseEntity.ok(repo.findAllOffered());
    }

    // Allow authenticated users only
    @Secured(ConstantsService.AUTHENTICATED_USER)
    // Match post mappings to /offered accepting application/x-www-form-urlencoded
    @PostMapping(value = "/offered", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> makeItem(Item.ItemDTO itemDTO) {
        // Return value returned from the service when provided with an empty offered item and the content received
        return service.makeItem(Item.offeredItem(), itemDTO);
    }

}