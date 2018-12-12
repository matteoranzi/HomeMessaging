package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 13.45
 */
public abstract class Message implements Serializable {
    protected MessageType messageType;
    protected String sourceUserID;

    public abstract String getText();

    public Message(String sourceUserID, MessageType messageType){
        this.sourceUserID = sourceUserID;
        this.messageType = messageType;
    }

    public  MessageType getMessageType(){
        return this.messageType;
    }

    public void setMessageType(MessageType messageType){
        this.messageType = messageType;
    }

    public String getSourceUserID(){
        return sourceUserID;
    }
}