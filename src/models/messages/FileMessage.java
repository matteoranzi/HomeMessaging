package models.messages;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 12/12/18
 * Time: 23.20
 */
public class FileMessage extends Message {
    private String fileName;
    private double percentageFile;

    public FileMessage(String sourceUserID, String fileName, MessageType messageType){
        super(sourceUserID, messageType);
        this.fileName = fileName;

        percentageFile = -1.0;//indeterminate value
    }

    public double getPercentageFile() {
        return percentageFile;
    }

    public void setPercentageFile(double percentageFile) {
        this.percentageFile = percentageFile;
    }

    @Override
    public String getText() {
        return fileName;
    }
}