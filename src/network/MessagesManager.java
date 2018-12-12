package network;

import models.messages.*;
import models.user.Status;
import models.user.User;
import models.user.UsersList;
import scenes.home.HomeController;
import utils.AppParameters;
import utils.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 13.42
 */
public class MessagesManager implements Runnable{

    private HomeController homeController;
    private boolean ready;
    private User localUser;
    private Log log;

    public MessagesManager(HomeController homeController, User localUser){
        this.localUser = localUser;
        this.homeController = homeController;

        log = new Log(this.getClass(), Log.DEBUG);

        ready = false;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(AppParameters.TCP_LISTENING_SERVER_PORT);
            ready = true;
            log.debug("Server is ready");
            while (true){
                Socket socket = serverSocket.accept();
                MessageHandler messageHandler = new MessageHandler(socket);
                messageHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady(){
        return this.ready;
    }

    public void start(){
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void sendTextMessage(User destinationUser, Message message){
        Thread t = new Thread(() -> {
            try {
                Socket socket = new Socket(destinationUser.getIp(), AppParameters.TCP_WRITING_SERVER_PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(message);

                oos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    //TODO update progressionBar
    public void sendFileMessage(User destinationUser, Message message, String filePath){
        Thread t = new Thread(() -> {
            try{
                Socket socket = new Socket(destinationUser.getIp(), AppParameters.TCP_WRITING_SERVER_PORT);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(message);
                oos.flush();

                FileSender fileSender = new FileSender(socket, filePath);
                fileSender.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class MessageHandler implements Runnable{
        Log log;
        private Socket socket;

        public MessageHandler(Socket socket){
            log = new Log(this.getClass(), Log.DEBUG);
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                Message message = (Message)ois.readObject();
                message.setMessageType(MessageType.RECEIVED);
                log.debug("Message received. Class -> " + message.getClass());
                if(message instanceof HelloResponseMessage){
                    handleHelloResponseMessage((HelloResponseMessage)message);
                }else if(message instanceof TextMessage){
                    handleTextMessage((TextMessage) message);
                }else if(message instanceof LogOutMessage){
                    handleLogOutMessage((LogOutMessage) message);
                }else if(message instanceof FileMessage){
                    handleFileMessage((FileMessage) message);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //TODO update progressionBar
        private void handleFileMessage(FileMessage message){
            User user = homeController.getUsersList().getUserByID(message.getSourceUserID());
            if(user == UsersList.NO_USER_FOUND){
                log.error("No user found in usersList");
            }else{
                homeController.addReceivedMessage(user, message);
                FileReceiver fileReceiver= new FileReceiver(socket, getPathForFile(message), message.getText());
                fileReceiver.start();
            }
        }

        private String getPathForFile(Message message){
            String user = homeController.getUsersList().getUserByID(message.getSourceUserID()).getUsername();
            String path =  Paths.get(System.getProperty("user.home"), AppParameters.APP_NAME, user).toString();
            log.debug("Create path for received file" + path);

            return path;
        }

        private void handleLogOutMessage(LogOutMessage message){
            User user = homeController.getUsersList().getUserByID(message.getSourceUserID());
            if(user == UsersList.NO_USER_FOUND){
                log.error("No user found during int logout handling");
            }else{
                user.setStatus(Status.UNREACABLE);
                homeController.updateChatView();
            }
        }

        private void handleTextMessage(TextMessage message){
            User user = homeController.getUsersList().getUserByID(message.getSourceUserID());
            if(user == UsersList.NO_USER_FOUND){
                log.error("No user found in usersList");
            }else{
                homeController.addReceivedMessage(user, message);
            }
        }

        private void handleHelloResponseMessage(HelloResponseMessage message){
            try {
                User newUser = message.getUser();
                newUser.setIp(InetAddress.getByName(socket.getInetAddress().getHostAddress()));

                if(localUser.getChatCode().equals(newUser.getChatCode())){
                    homeController.addUserToList(newUser);
                    log.debug("Added user from = " + newUser.getIp() + " \t|\tID: " + newUser.getID());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(localUser);
                }else{
                    log.debug("Added has different chatCode");
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(null);// client isn't interested in having my data because chatCode is different
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start(){
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }
    }
}