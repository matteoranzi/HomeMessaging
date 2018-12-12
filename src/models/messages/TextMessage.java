package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 19.23
 */
public class TextMessage extends Message {
    private String text;

    public TextMessage(String sourceUserID, String text, MessageType messageType){
        super(sourceUserID, messageType);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}