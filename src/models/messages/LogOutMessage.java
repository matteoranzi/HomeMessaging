package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 22.16
 */
public class LogOutMessage implements Message, Serializable {
    private MessageType messageType;
    private String sourceUserID;

    public LogOutMessage(String sourceUserID, MessageType messageType){
        this.sourceUserID = sourceUserID;
        this.messageType = messageType;
    }

    @Override
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String getText() {
        return "Disconnected user (ID): " + sourceUserID;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    public String getSourceUserID(){
        return sourceUserID;
    }
}