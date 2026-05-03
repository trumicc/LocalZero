package com.localzero.localzero.communication;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.Notification;

//mediator interface (colleagues communicate through it)
public interface CommunicationHub {
    void register(Participant participant);
    void deregister(String userId);
    void sendMessage(Message message);
    void sendNotification(Notification notification);
}
