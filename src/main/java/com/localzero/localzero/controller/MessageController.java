package com.localzero.localzero.controller;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.service.MessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
