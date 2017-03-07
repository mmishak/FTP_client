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

    }

    public void sendMessage(String message) throws IOException {
        Util.log("--> " + message, Util.LOG_TYPE_COMMAND);
        out.write(message + "\n");
        out.flush();
    }

    public void recvReply() {
        try {
            StringBuilder sb = new StringBuilder();
            String line = in.readLine();

            replyCode = Integer.valueOf(line.substring(0, 3));
            sb.append(line).append("\n");

            while (!line.startsWith(replyCode + " ")) {
                line = in.readLine();
                sb.append(line).append("\n");
            }

            reply = sb.toString().trim();

            Util.log(reply,Util.LOG_TYPE_ANSWER);

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
