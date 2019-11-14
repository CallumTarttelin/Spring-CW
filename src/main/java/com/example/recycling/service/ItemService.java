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

    public ItemService(ItemRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }


    public ResponseEntity<Void> makeItem(Item base, Item.ItemDTO itemDTO) {
        Item item = base
                .setCondition(itemDTO.getCondition())
                .setDescription(itemDTO.getDescription())
                .setListUntilDate(itemDTO.getListUntilDate())
                .setCategory(itemDTO.getCategories())
                .setUser(UserProvider.getUser());
        Item saved = repo.save(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Void> askQuestion(String id, Question.QuestionDTO questionDTO) {
        Optional<Item> optionalItem = repo.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        Question question = new Question()
                .setMessage(questionDTO.getMessage())
                .setSentBy(UserProvider.getUser());
        item.getQuestions().add(question);
        repo.save(item);
        emailService.sendQuestionEmail(item, question);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/item/{id}")
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Void> answerQuestion(String itemId, String questionId, Question.QuestionDTO questionDTO) {
        Optional<Item> optionalItem = repo.findById(itemId);
        Optional<Question> optionalQuestion = optionalItem.flatMap( item ->
                item.getQuestions().stream().filter(question -> question.getId().equals(questionId)).findFirst()
        );
        if (optionalQuestion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item item = optionalItem.get();
        Question question = optionalQuestion.get();
        Response response = new Response()
                .setMessage(questionDTO.getMessage())
                .setSentBy(UserProvider.getUser());
        question.getResponses().add(response);
        repo.save(item);
        emailService.sendResponseEmail(question, response);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/api/item/{id}")
                .buildAndExpand(itemId).toUri();
        return ResponseEntity.created(location).build();
    }
}
