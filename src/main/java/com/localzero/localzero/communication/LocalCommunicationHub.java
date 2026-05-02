package com.localzero.localzero.communication;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.Notification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//Concrete mediator
@Service
public class LocalCommunicationHub implements CommunicationHub{

    private Map<String, Participant> participants = new HashMap<>();

    @Override
    public void register(Participant participant) {
        participants.put(participant.getUserId(), participant);
    }

    @Override
    public void sendMessage(Message message) {
        Participant receiver = participants.get(message.getReceiverId());
        if (receiver == null) {
            return;
        }
        receiver.receiveMessage(message);
    }

    @Override
    public void sendNotification(Notification notification) {
        Participant participant = participants.get(notification.getUserId());
        if (participant == null) {
            return;
        }
        participant.receiveNotification(notification);
    }
}
