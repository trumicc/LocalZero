package com.localzero.localzero.communication;


import com.localzero.localzero.model.Message;
import com.localzero.localzero.model.Notification;
import lombok.Getter;

//abstract colleague holding a reference to the hub
public abstract class Participant {

    @Getter
    private String userId;
    @Getter
    private CommunicationHub hub;

    public Participant(String userId, CommunicationHub hub) {
        this.userId = userId;
        this.hub = hub;
    }

    public abstract void receiveMessage(Message message);

    public abstract void receiveNotification(Notification notification);
}
