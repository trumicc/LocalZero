package com.localzero.localzero.service;

import com.localzero.localzero.communication.CommunicationHub;
import com.localzero.localzero.communication.UserParticipant;
import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.User;
import com.localzero.localzero.repository.MessageRepository;
import com.localzero.localzero.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final CommunicationHub hub;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(CommunicationHub communicationHub, MessageRepository messageRepository, UserRepository userRepository) {
        this.hub = communicationHub;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;

    }

    public void sendMessage(Message message) {
        User receiver = userRepository.findByEmail(message.getReceiverEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        message.setReceiverId(String.valueOf(receiver.getId()));
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        hub.sendMessage(message);
    }

    public List<Message> getInbox(String userId){
        return messageRepository.findByReceiverId(userId);
    }

}
