package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 19.23
 */
public class TextMessage implements Message, Serializable {
    private MessageType messageType;
    private String sourceUserID;
    private String text;

    public TextMessage(String sourceUserID, String text, MessageType messageType){
        this.text = text;
        this.sourceUserID = sourceUserID;
        this.messageType = messageType;
    }

    @Override
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public String getSourceUserID(){
        return sourceUserID;
    }
}