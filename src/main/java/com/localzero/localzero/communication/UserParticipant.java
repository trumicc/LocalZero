package com.localzero.localzero.communication;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.Notification;
import com.localzero.localzero.repository.MessageRepository;
import com.localzero.localzero.repository.NotificationRepository;

//sending through the hub instead of talking directly to other participants.
public class UserParticipant extends Participant{

    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;

    public UserParticipant(String userId, CommunicationHub hub, MessageRepository messageRepository, NotificationRepository notificationRepository) {
        super(userId, hub);
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
    }


    @Override
    public void receiveMessage(Message message) {
        message.setDelivered(true);
        messageRepository.save(message);
    }

    @Override
    public void receiveNotification(Notification notification) {
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
