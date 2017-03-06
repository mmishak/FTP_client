package logic;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by mmishak on 01/03/17.
 */
public class FtpControlSocket extends Socket{

    private BufferedWriter out;
    private BufferedReader in;
    private String reply = "";
    private int replyCode = 0;

    public FtpControlSocket(String host, int port) throws IOException {
        super(InetAddress.getByName(host), port);

        out = new BufferedWriter(new OutputStreamWriter(this.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(this.getInputStream()));

        recvReply();

    }

    public void sendMessage(String message) throws IOException {
        System.out.println("--> " + message);
        out.write(message + "\n");
        out.flush();
    }

    public void recvReply() {
        try {
            StringBuilder sb = new StringBuilder();
            String line = "";

            while (true) {
                sb.append(line = in.readLine()).append("\n");
                if (line.charAt(3) == ' ') break;
            }

            reply = sb.toString();
            replyCode = Integer.valueOf(line.substring(0, 3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getReplyCode() {
        return replyCode;
    }

    public String getReply(){
        return reply;
    }

}
