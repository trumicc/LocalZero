package com.localzero.localzero.controller;


import com.localzero.localzero.model.Notification;
import com.localzero.localzero.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> notify(@RequestBody Notification notification, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        }
        service.notifyUser(notification);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Notification> getNotifications(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return service.getUnread(String.valueOf(userId));
    }

    @PostMapping("/mark-all-read")
    public void markAllRead(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        service.markAllRead(String.valueOf(userId));
    }
}
