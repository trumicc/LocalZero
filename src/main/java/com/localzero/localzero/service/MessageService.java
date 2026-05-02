package com.localzero.localzero.service;

import com.localzero.localzero.communication.CommunicationHub;
import com.localzero.localzero.communication.UserParticipant;
import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.User;
import com.localzero.localzero.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final CommunicationHub hub;
    private final MessageRepository messageRepository;

    public MessageService(CommunicationHub communicationHub, MessageRepository messageRepository) {
        this.hub = communicationHub;
        this.messageRepository = messageRepository;

    }

    public void sendMessage(Message message) {
        messageRepository.save(message);
        hub.sendMessage(message);
    }

}
