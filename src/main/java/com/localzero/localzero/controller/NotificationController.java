package com.localzero.localzero.controller;


import com.localzero.localzero.model.Notification;
import com.localzero.localzero.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public void notify(@RequestBody Notification notification) {
        service.notifyUser(notification);
    }
}
