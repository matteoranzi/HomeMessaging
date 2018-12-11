package models.user;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 27/11/18
 * Time: 0.41
 */
public class User implements Serializable {
    static public final String DEFAULT_CHAT_CODE = "DEFAULT";

    private Status status = Status.REACHABLE;

    private String username;
    private String chatCode;
    private InetAddress ip;
    private String id;

    public User(String username, String chatCode, InetAddress ip){
        this.username = username;
        this.chatCode = chatCode;
        this.ip = ip;
        this.id = UUID.randomUUID().toString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getChatCode() {
        return chatCode;
    }

    public InetAddress getIp() {
        return ip;
    }
    public void setIp(InetAddress ip){
        this.ip = ip;
    }

    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return "{username: " + username + ", chatCode: " + chatCode + ", ip: " + ip + ", id: " + id + "}";
    }
}