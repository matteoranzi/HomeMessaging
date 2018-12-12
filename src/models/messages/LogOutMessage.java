package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 22.16
 */
public class LogOutMessage extends Message {
    public LogOutMessage(String sourceUserID, MessageType messageType){
        super(sourceUserID, messageType);
        this.sourceUserID = sourceUserID;
    }

    @Override
    public String getText() {
        return "Disconnected user (ID): " + sourceUserID;
    }
}