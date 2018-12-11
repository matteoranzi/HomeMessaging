package network;

import utils.AppParameters;
import utils.Log;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 14.30
 */
public class HelloMessageSender implements Runnable{
    private Log log;
    private final String ID;

    public HelloMessageSender(String ID){
        log = new Log(this.getClass(), Log.DEBUG);
        this.ID = ID;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            ArrayList<InetAddress> broadcastAddresses = getBroadcastAddresses();

            //Send Hello packet to all broadcast addresses available
            for(InetAddress broadcastAddress : broadcastAddresses) {
                DatagramPacket datagramPacket = new DatagramPacket(ID.getBytes(), ID.getBytes().length, broadcastAddress, AppParameters.UDP_WRITING_SERVER_PORT);
                datagramSocket.send(datagramPacket);

                log.debug("Send broadcast -> " + broadcastAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<InetAddress> getBroadcastAddresses(){
        ArrayList<InetAddress> broadcastAddresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();

                List<InterfaceAddress> list = ni.getInterfaceAddresses();

                for (InterfaceAddress ia : list) {
                    if (ia.getBroadcast() != null) {
                        broadcastAddresses.add(ia.getBroadcast());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return broadcastAddresses;
    }

    public void start(){
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
}