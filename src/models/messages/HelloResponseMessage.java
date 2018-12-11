package models.messages;

import models.user.User;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 13.58
 */
public class HelloResponseMessage implements Message, Serializable {
    private MessageType messageType;
    private User user;
    public HelloResponseMessage(User user, MessageType messageType){
        this.user = user;
        this.messageType = messageType;
    }

    @Override
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String getText() {
        return "Connected user: " + user.getUsername();
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public User getUser() {
        return user;
    }
}