package utils;

/**
 * Created by IntelliJ IDEA.
 * User: matteoranzi
 * Date: 03/12/18
 * Time: 20.37
 */

public class Log {
    public static int ALL = 0;
    public static int DEBUG = 1;
    public static int INFO = 2;
    public static int WARN = 3;
    public static int ERROR = 4;
    public static int FATAL = 5;
    public static int OFF = 6;

    private int level;
    private Class c;

    public Log(Class c, int level){
        this.c = c;
        this.level = level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void debug(String message){
        if(level <= DEBUG){
            System.out.println("DEBUG - " + c.getName() + " -> " + message);
        }
    }

    public void info(String message){
        if(level <= INFO){
            System.out.println("INFO - " + c.getName() + " -> " + message);
        }
    }

    public void warn(String message){
        if(level <= WARN){
            System.out.println("WARN - " + c.getName() + " -> " + message);
        }
    }

    public void error(String message){
        if(level <= ERROR){
            System.out.println("ERROR - " + c.getName() + " -> " + message);
        }
    }

    public void fatal(String message){
        if(level <= FATAL){
            System.out.println("FATAL - " + c.getName() + " -> " + message);
        }
    }
}