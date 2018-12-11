package models.chat;

import models.user.User;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 18.59
 */
public class ChatsList {
    public static final Chat USER_CHAT_NOT_FOUND = null;

    private ArrayList<Chat> chats;

    public ChatsList(){
        chats = new ArrayList<>();
    }

    public void addChat(Chat chat){
        chats.add(chat);
    }

    public Chat getChat(User user){
        for(Chat chat: chats){
            if(chat.getUser() == user){
                return chat;
            }
        }

        return USER_CHAT_NOT_FOUND;
    }

    public void createChat(User user){
        addChat(new Chat(user));
    }
}