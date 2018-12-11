package models.chat;

import models.messages.MessagesList;
import models.messages.Message;
import models.user.User;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 18.54
 */
public class Chat {
    private int notificationCounter;
    private MessagesList messagesList;
    private User user;

    public Chat(User user){
        messagesList = new MessagesList();
        this.user = user;
        notificationCounter = 0;
    }

    public int getNotificationCounter(){
        return notificationCounter;
    }

    public User getUser(){
        return user;
    }

    public ArrayList<Message> getMessages(){
        return messagesList.getMessages();
    }

    public void incrementNotificationCounter(){
        notificationCounter++;
    }

    public void resetNotificationCounter(){
        notificationCounter = 0;
    }

    public void addMessage(Message message){
        messagesList.addMessage(message);
    }
}