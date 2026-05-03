package com.localzero.localzero.service;

import com.localzero.localzero.communication.CommunicationHub;
import com.localzero.localzero.model.Notification;
import com.localzero.localzero.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final CommunicationHub hub;
    private final NotificationRepository notificationRepository;

    public NotificationService(CommunicationHub hub, NotificationRepository notificationRepository) {
        this.hub = hub;
        this.notificationRepository = notificationRepository;
    }

    public void notifyUser(Notification notification) {
        notificationRepository.save(notification);
        hub.sendNotification(notification);
    }

    public List<Notification> getUnread(String userId){
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

}
