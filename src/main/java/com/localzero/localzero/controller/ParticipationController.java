package com.localzero.localzero.controller;

import com.localzero.localzero.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@RestController
@RequestMapping("/api/participation")
public class ParticipationController {

    @Autowired
    private ParticipationService participationService;

    @PostMapping("/join/{initiativeId}")
    public ResponseEntity<?> join(@PathVariable int initiativeId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        participationService.join(userId, initiativeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unjoin/{initiativeId}")
    public ResponseEntity<?> unjoin(@PathVariable int initiativeId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        participationService.unjoin(userId, initiativeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/postUpdate")
    public ResponseEntity<?> update(
            @RequestParam int initiativeId,
            @RequestParam String updateContent,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        participationService.postUpdate(userId, updateContent, initiativeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment")
    public ResponseEntity<?> comment(
            @RequestParam int updateId,
            @RequestParam String commentContent,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        participationService.comment(commentContent, updateId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestParam int updateId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        participationService.like(updateId, userId);
        return ResponseEntity.ok().build();
    }
}