package com.localzero.localzero.communication;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.Notification;

//sending through the hub instead of talking directly to other participants.
public class UserParticipant extends Participant{


    public UserParticipant(String userId, CommunicationHub hub){
        super(userId, hub);
    }

    public void sendMessage(Message message){
        getHub().sendMessage(message);
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println("User " + getUserId() + " received message: " + message.getContent());
    }

    @Override
    public void receiveNotification(Notification notification) {
        System.out.println("User " + getUserId() + " received notification: " + notification.getContent());
    }
}
