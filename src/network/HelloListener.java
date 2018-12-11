package network;

import models.messages.HelloResponseMessage;
import models.messages.MessageType;
import models.user.User;
import scenes.home.HomeController;
import utils.AppParameters;
import utils.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 12.36
 */
public class HelloListener implements Runnable {

    private HomeController homeController;
    private boolean ready;
    private Log log;
    private User localUser;

    public HelloListener(HomeController homeController, User localUser){
        this.homeController = homeController;

        this.localUser = localUser;

        log = new Log(this.getClass(), Log.DEBUG);

        ready = false;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(AppParameters.UDP_LISTENING_SERVER_PORT);
            int DATAGRAM_PACKET_SIZE = 1024;
            byte[] bytes = new byte[DATAGRAM_PACKET_SIZE];

            ready = true;//server is ready
            log.debug("Server is ready");

            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                datagramSocket.receive(datagramPacket);
                MessageHandler messageHandler = new MessageHandler(datagramPacket);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class MessageHandler implements Runnable{
        private DatagramPacket datagramPacket;

        public MessageHandler(DatagramPacket datagramPacket){
            this.datagramPacket = datagramPacket;
        }

        @Override
        public void run() {
            byte[] receivedIDByteArray = datagramPacket.getData();
            String receivedID = new String(receivedIDByteArray, 0, datagramPacket.getLength());//the chatCode is expected

            log.debug("Handling message from: " + datagramPacket.getSocketAddress().toString());
            log.debug("New host ID -> " + receivedID.replace("\n", "") + " while my ID -> " + localUser.getID());

            //if the packet isn't mine then create the connection
            if(!receivedID.equals(localUser.getID())){//TODO check if the host isn't already present
                log.debug("This isn't me");
                sendHelloResponse(datagramPacket.getAddress());
            }
        }

        private void sendHelloResponse(InetAddress address){
            try {
                Socket socket = new Socket(address, AppParameters.TCP_WRITING_SERVER_PORT);
                HelloResponseMessage sendMessage = new HelloResponseMessage(localUser, MessageType.SENT);

                //Send user information
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(sendMessage);

                oos.flush();

                //Receive user information
                User newUser;
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                if((newUser = (User) ois.readObject()) != null){//if the other user responses with null means that has a different chatCode
                    newUser.setIp(address);
                    homeController.addUserToList(newUser);
                    log.debug("Added user --> " + newUser.toString() + " \t|\t ID: " + newUser.getID());
                }else{
                    log.debug("The user had a different chatCode -> not added to list");
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void start(){
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }
}