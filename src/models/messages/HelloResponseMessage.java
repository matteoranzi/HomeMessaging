package models.messages;

import models.user.User;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 13.58
 */
public class HelloResponseMessage extends Message {
    private User user;

    public HelloResponseMessage(User user, MessageType messageType){
        super(user.getID(), messageType);
        this.user = user;
    }

    @Override
    public String getText() {
        return "Connected user: " + user.getUsername();
    }

    public User getUser() {
        return user;
    }
}