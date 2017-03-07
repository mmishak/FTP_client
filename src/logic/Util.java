package logic;


import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by mmishak on 28/02/17.
 */
public class Util {

    private static String localAddress = "127.0.0.1";

    public static String getLocalAddress() {
        return localAddress;
    }

    public static void setLocalAddress(String address){
        localAddress = address;
    }

    public static int getAvailablePort(int from, int to) {
        for (int i = from; i < to; i++) {
            try {
                new ServerSocket(i).close();
                return i;
            }catch (IOException e){
                continue;
            }
        }

        return -1;
    }

    private static JTextArea logArea = new JTextArea();

    public static void setLogArea(JTextArea logArea){
        Util.logArea = logArea;
    }

    public static final int LOG_TYPE_COMMAND = 0;
    public static final int LOG_TYPE_ANSWER = 1;
    public static void log(String message, int type){
        logArea.append(message);
        logArea.append("\n");
        logArea.setCaretPosition(logArea.getText().length());
    }
}
