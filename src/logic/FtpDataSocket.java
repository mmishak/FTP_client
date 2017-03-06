package logic;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mmishak on 04/03/17.
 */
public class FtpDataSocket extends ServerSocket implements Runnable {

    public static final int SEND_FILE_MODE = 1;
    public static final int RECV_LIST_MODE = 0;
    public static final int RECV_FILE_MODE = 2;

    private BufferedOutputStream outStream;
    private BufferedInputStream inStream;
    private BufferedReader inReader;
    private int connectMode = 0; // 0 - recive, 1 - send
    private Socket socket;
    private String listData = "";
    private File fileData = new File("");

    public FtpDataSocket(int port) throws IOException {
        super(port);
    }

    public void acceptConnection() throws IOException {
        socket = this.accept();
    }

    public void closeConnection() throws IOException {
        socket.close();
    }

    public String getListData(){
        return listData;
    }

    public void setFileData(File file) throws IOException {
        this.fileData = file;
    }

    public File getFileData(){
        return fileData;
    }

    private void recvListData() throws IOException {
        inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = inReader.readLine()) != null && !socket.isClosed()){
            sb.append(line).append('\n');
        }

        listData = sb.toString();

        inReader.close();
    }

    private void recvFileData() throws IOException{

        inStream = new BufferedInputStream(socket.getInputStream());
        outStream = new BufferedOutputStream(new FileOutputStream(fileData));

        int temp;
        while ((temp = inStream.read()) != -1){
            outStream.write(temp);
        }

        inStream.close();
        outStream.close();
    }

    private void sendFileData() throws IOException{
        inStream = new BufferedInputStream(new FileInputStream(fileData));
        outStream = new BufferedOutputStream(socket.getOutputStream());

        int temp;
        while ((temp = inStream.read()) != -1){
            outStream.write(temp);
        }

        inStream.close();
        outStream.close();
    }

    public void setConnectMode(int mode) {
        connectMode = mode;
    }

    public void start(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            acceptConnection();
            switch (connectMode) {
                case 0:
                    recvListData();
                    break;
                case 1:
                    sendFileData();
                    break;
                case 2:
                    recvFileData();
                    break;
                default:
                    break;
            }
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
