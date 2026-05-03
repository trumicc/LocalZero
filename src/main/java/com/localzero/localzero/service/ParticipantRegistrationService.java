package com.localzero.localzero.service;

import com.localzero.localzero.communication.CommunicationHub;
import com.localzero.localzero.communication.UserParticipant;
import com.localzero.localzero.repository.MessageRepository;
import com.localzero.localzero.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipantRegistrationService {
    private final CommunicationHub hub;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;

    public ParticipantRegistrationService(CommunicationHub hub, MessageRepository messageRepository, NotificationRepository notificationRepository) {
        this.hub = hub;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
    }

    public void registerUser(String userId) {
        hub.register(new UserParticipant(userId, hub, messageRepository, notificationRepository));
    }

    public void deregisterUser(String userId) {
        hub.deregister(userId);
    }
}
