package com.localzero.localzero.controller;

import com.localzero.localzero.model.Initiative;
import com.localzero.localzero.service.InitiativeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/initiatives")
public class InitiativeController {

    private final InitiativeService initiativeService;

    public InitiativeController(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllInitiatives(HttpSession session) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        List<Initiative> initiatives = initiativeService.getVisibleInitiativesForUser(userId);
        return ResponseEntity.ok(initiatives);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInitiativeById(
            @PathVariable int id,
            HttpSession session
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        try {
            Initiative initiative = initiativeService.getInitiativeById(id, userId);
            return ResponseEntity.ok(initiative);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createInitiative(
            @RequestBody Initiative initiative,
            HttpSession session
    ) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        try {
            Initiative savedInitiative = initiativeService.createInitiative(initiative, userId);
            return ResponseEntity.ok(savedInitiative);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Long getLoggedInUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}