package models.messages;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 13.45
 */
public interface Message {
    MessageType getMessageType();
    void setMessageType(MessageType messageType);
    String getText();
}