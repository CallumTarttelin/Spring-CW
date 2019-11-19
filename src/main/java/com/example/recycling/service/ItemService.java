package com.example.recycling.service;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.entity.Response;
import com.example.recycling.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository repo;

    private final EmailService emailService;

    // Create service with repo and emailService
    public ItemService(ItemRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }


    public ResponseEntity<Void> makeItem(Item base, Item.ItemDTO itemDTO) {
        // Create item from DTO details
        Item item = base
                .setCondition(itemDTO.getCondition())
                .setDescription(itemDTO.getDescription())
                .setListUntilDate(itemDTO.getListUntilDate())
                .setCategory(itemDTO.getCategories())
                .setUser(UserProvider.getUser());
        // save item
        Item saved = repo.save(item);
        // Get location for current request, add /{id} to it, replace {id} with item id, make it into a URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Void> askQuestion(String id, Question.QuestionDTO questionDTO) {
        // Get item by id and check it is present
        Optional<Item> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            // Send 404 if can't find item
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        // Create a new question
        Question question = new Question()
                .setMessage(questionDTO.getMessage())
                .setSentBy(UserProvider.getUser());
        // Add question to item and save
        item.getQuestions().add(question);
        repo.save(item);
        // Send email to item owner about this question
        emailService.sendQuestionEmail(item, question);
        // Get location for current request, add /{id} to it, replace {id} with item id, make it into a URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/item/{id}")
                .buildAndExpand(id).toUri();
        // Return 201 with the location of item
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Void> answerQuestion(String itemId, String questionId, Question.QuestionDTO questionDTO) {
        // Get item by id and check it is present
        Optional<Item> optionalItem = repo.findById(itemId);
        // If the item is present get the question
        Optional<Question> optionalQuestion = optionalItem.flatMap( item ->
                item.getQuestions().stream().filter(question -> question.getId().equals(questionId)).findFirst()
        );
        if (optionalQuestion.isEmpty()) {
            // Send 404 if the question doesn't exist or item doesn't exist
            return ResponseEntity.notFound().build();
        }
        // Extract item and question from optionals
        Item item = optionalItem.get();
        Question question = optionalQuestion.get();
        // Create response from details, add it to question and save item
        Response response = new Response()
                .setMessage(questionDTO.getMessage())
                .setSentBy(UserProvider.getUser());
        question.getResponses().add(response);
        repo.save(item);
        // Send email to question owner about this response
        emailService.sendResponseEmail(question, response);
        // Get location for current request, add /{id} to it, replace {id} with item id, make it into a URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/item/{id}")
                .buildAndExpand(itemId).toUri();
        // Return 201 with location of item
        return ResponseEntity.created(location).build();
    }
}
