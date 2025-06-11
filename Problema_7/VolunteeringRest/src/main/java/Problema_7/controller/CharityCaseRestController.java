package Problema_7.controller;

import Problema_7.model.domain.CharityCase;
import Problema_7.persistence.repository.CharityCaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import Problema_7.model.domain.ResourceEvent;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/charity-cases")
public class CharityCaseRestController {

    private final CharityCaseRepo charityCaseRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CharityCaseRestController(CharityCaseRepo charityCaseRepo, SimpMessagingTemplate messagingTemplate) {
        this.charityCaseRepo = charityCaseRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/test")
    public String test(@RequestParam(value = "msg", defaultValue = "Hello") String msg) {
        return ("CharityCase API - test: " + msg).toUpperCase();
    }


    @GetMapping
    public Iterable<CharityCase> getAll() {
        return charityCaseRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharityCase> getById(@PathVariable int id) {
        Optional<CharityCase> result = charityCaseRepo.findOne(id);
        return result.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<CharityCase> create(@RequestBody CharityCase newCase) {
        newCase.setId(null);
        System.out.println("Creating new charity case: " + newCase);
        Optional<CharityCase> saved = charityCaseRepo.save(newCase);
        saved.ifPresent(c -> messagingTemplate.convertAndSend("/topic/cases",
                new ResourceEvent("create", c)));
        return saved.map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<CharityCase> update(@PathVariable int id, @RequestBody CharityCase updatedCase) {
        Optional<CharityCase> updated = charityCaseRepo.update(updatedCase, id);
        updated.ifPresent(c -> messagingTemplate.convertAndSend("/topic/cases",
                new ResourceEvent("update", c)));
        return updated.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Optional<CharityCase> existing = charityCaseRepo.findOne(id);
        if (existing.isPresent()) {
            charityCaseRepo.delete(id);
            messagingTemplate.convertAndSend("/topic/cases",
                    new ResourceEvent("delete", existing.get()));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
