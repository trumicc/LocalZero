package com.localzero.localzero.controller;

import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.service.InitiativeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/initiatives")
public class InitiativeController {

    private final InitiativeService initiativeService;

    public InitiativeController(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }

    @GetMapping
    public ResponseEntity<List<Initiative>> getAllInitiatives(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Initiative> initiatives = initiativeService.getVisibleInitiativesForUser(userId);
        return ResponseEntity.ok(initiatives);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Initiative> getInitiativeById(
            @PathVariable int id,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Initiative initiative = initiativeService.getInitiativeById(id, userId);
        return ResponseEntity.ok(initiative);
    }

    @PostMapping
    public ResponseEntity<Initiative> createInitiative(
            @RequestBody Initiative initiative,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Initiative savedInitiative = initiativeService.createInitiative(initiative, userId);
        return ResponseEntity.ok(savedInitiative);
    }
}