package utils.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 10/12/18
 * Time: 12.52
 */
public class NotInitializedClassException extends Exception {
    public NotInitializedClassException(String message){
        super(message);
    }
}