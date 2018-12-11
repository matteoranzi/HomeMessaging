package models.user;

import utils.Log;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 12.40
 */
public class UsersList {
    public static final boolean USER_ALREADY_PRESENT = false;
    public static final User NO_USER_FOUND = null;

    private Log log;
    private ArrayList<User> users;

    public UsersList(){
        this.users = new ArrayList<>();
        log = new Log(this.getClass(), Log.DEBUG);
    }

    public synchronized boolean addUser(User user){
        for(User u: users){
            if(u.getID().equals(user.getID())){
                return USER_ALREADY_PRESENT;
            }
        }

        users.add(user);
        return !USER_ALREADY_PRESENT;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public User getUser(int i){
        return users.get(i);
    }

    public User getUserByIP(InetAddress address){
        for(User user: users){
            if(user.getIp().equals(address)){
                return user;
            }
        }

        return NO_USER_FOUND;
    }

    public User getUserByID(String id){
        for(User user: users){
            if(user.getID().equals(id)){
                return user;
            }
        }

        return NO_USER_FOUND;
    }

    public void removeUser(User user){
        if(users.remove(user)){
            log.debug("User correctly removed from userList");
        }
    }
}
