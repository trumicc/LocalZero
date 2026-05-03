package com.localzero.localzero.controller;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @PostMapping
    public void sendMessage(@RequestBody Message message) {
        service.sendMessage(message);
    }

    @GetMapping("/inbox")
    public List<Message> getInbox(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return service.getInbox(String.valueOf(userId));
    }
}
