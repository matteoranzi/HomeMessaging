package network;

import javafx.application.Platform;
import utils.ByteUtils;
import utils.Log;

import java.io.*;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 12/12/18
 * Time: 21.48
 */

//      +-------------------------------------------+
//      |                                           |
//      |           ***PROTOCOL STRUCTURE***        |
//      |                                           |
//      |   1 byte -> length file name in bytes (X) |
//      |   X byte -> file name                     |
//      |   8 byte -> length file in bytes (Y)      |  -> no more necessary thanks to buffered INPUT/OUTPUT streams
//      |   Y byte -> file                          |
//      |                                           |
//      +-------------------------------------------+
// TODO: 12/09/18 support multiple file sender
// TODO: 12/09/18 support directory sender

//TODO in file manager check the status with a polling | or implement a callback

public class FileSender implements Runnable {
    private Log log;

    private Socket socket;
    private String filePath;

    public FileSender(Socket socket, String filePath){
        this.socket = socket;
        this.filePath = filePath;

        this.log = new Log(this.getClass(), Log.DEBUG);
    }

    @Override
    public void run() {
        try {
            File file = new File(filePath);
            BufferedInputStream fileBufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream socketBufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());

            long fileToSendSize = file.length();
            byte[] fileNameBytesArray = file.getName().getBytes();

            byte[] fileNameLengthBytesArray = new byte[1];
            fileNameLengthBytesArray[0] = (byte) fileNameBytesArray.length;

            socketBufferedOutputStream.write(fileNameLengthBytesArray, 0, fileNameLengthBytesArray.length);
            socketBufferedOutputStream.flush();
            socketBufferedOutputStream.write(fileNameBytesArray, 0, fileNameBytesArray.length);
            socketBufferedOutputStream.flush();
            socketBufferedOutputStream.write(ByteUtils.longToBytes(fileToSendSize));
            socketBufferedOutputStream.flush();

            byte[] buffer = new byte[1024];// TODO: 19/09/18 chose the size of the buffer
            int bytesRead;

            while((bytesRead = fileBufferedInputStream.read(buffer)) != -1){
                socketBufferedOutputStream.write(buffer, 0, bytesRead);
                socketBufferedOutputStream.flush();
            }

            socketBufferedOutputStream.flush();//with this fucking flush change everything (it works)! TO REMEMBER!!!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getStatusPercentage(){
        return 0;//TODO implement
    }

    public void start(){
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
}