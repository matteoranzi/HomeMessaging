package network;

import utils.ByteUtils;
import utils.Log;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 12/12/18
 * Time: 21.47
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

public class FileReceiver implements Runnable{
    private Log log;

    private Socket socket;
    private String filePath;
    private String fileName;

    public FileReceiver(Socket socket, String filePath, String fileName){
        this.socket = socket;
        this.filePath = filePath;
        this.fileName = fileName;

        this.log = new Log(this.getClass(), Log.DEBUG);
    }

    @Override
    public void run() {
        //TODO check the return value of stream's reads
        try{
            log.debug("Writing file to: " + filePath + "--->" + fileName);

            File newDir = new File(filePath);
            newDir.mkdirs();
            //TODO check if the file already exists
            File file = new File(Paths.get(filePath, fileName).toString());
            BufferedOutputStream fileBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            BufferedInputStream socketBufferedInputStream = new BufferedInputStream(socket.getInputStream());

            byte[] fileNameLengthToReceiveBytesArray = new byte[1];//MAX_FILE_NAME_LENGTH = 255

            socketBufferedInputStream.read(fileNameLengthToReceiveBytesArray, 0, fileNameLengthToReceiveBytesArray.length);

            byte[] fileNameBytesArray = new byte[fileNameLengthToReceiveBytesArray[0]];
            socketBufferedInputStream.read(fileNameBytesArray, 0, fileNameBytesArray.length);

            byte[] fileLengthToReceiveBytesArray = new byte[8];
            socketBufferedInputStream.read(fileLengthToReceiveBytesArray, 0, fileLengthToReceiveBytesArray.length);
            // System.out.println("lol: " + ByteUtils.bytesToLong(ByteUtils.longToBytes(fileToSendSize)));
            long fileLengthToReceive = ByteUtils.bytesToLong(fileLengthToReceiveBytesArray);
            byte[] fileToReceiveBytesArray = new byte[(int)fileLengthToReceive];


            //Create buffer
            byte[] buffer = new byte[1024];// TODO: 19/09/18 chose the size of the buffer
            int bytesRead;

            while((bytesRead = socketBufferedInputStream.read(buffer)) != -1){
                fileBufferedOutputStream.write(buffer, 0, bytesRead);
                fileBufferedOutputStream.flush();

            }

            fileBufferedOutputStream.flush();
        }catch (IOException e){
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