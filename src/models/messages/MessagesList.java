package models.messages;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 18.00
 */
public class MessagesList {
    private ArrayList<Message> messages;

    public MessagesList(){
        messages = new ArrayList<>();
    }

    public ArrayList<Message> getMessages(){
        return messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }
}