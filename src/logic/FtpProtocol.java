package logic;

import java.io.*;
import java.net.UnknownHostException;

/**
 * Created by mmishak on 28/02/17.
 */
public class FtpProtocol {

    public static final int CONNECT_SUCCESS = 220;
    public static final int USER_SUCCESS = 331;
    public static final int PASS_SUCCESS = 230;
    public static final int PWD_SUCCESS = 257;
    public static final int PORT_SUCCESS = 200;
    public static final int NLST_SUCCESS = 226;
    public static final int LIST_SUCCESS = 226;
    public static final int RETR_SUCCESS = 226;
    public static final int STOR_SUCCESS = 226;
    public static final int MKD_SUCCESS = 257;
    public static final int CWD_SUCCESS = 250;
    public static final int QUIT_SUCCESS = 221;
    public static final int TYPE_SUCCESS = 200;
    public static final int DATA_CONNECT_SUCCESS = 150;
    public static final int RMD_SUCCESS = 250;
    public static final int DELE_SUCCESS = 250;

    private FtpControlSocket controlSoket;
    private FtpDataSocket dataSoket;

    public FtpProtocol() throws IOException {
        dataSoket = new FtpDataSocket();
    }

    private boolean weitReplyCode(int code) {
        return controlSoket.getReplyCode() == code;
    }

    public boolean connect(String hostname, int port) {
        try {

            Util.log("--> Try connect to " + hostname, Util.LOG_TYPE_COMMAND);

            controlSoket = new FtpControlSocket(hostname, port);

            controlSoket.recvReply();
            return weitReplyCode(FtpProtocol.CONNECT_SUCCESS);


        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean disconnect() {
        try {
            controlSoket.close();
            Util.log("--> Socket closed", Util.LOG_TYPE_COMMAND);
            return true;
        } catch (IOException e) {
            Util.log("--> Socket not closed", Util.LOG_TYPE_COMMAND);
            return false;
        }

    }

    //получение от клиента идентификационной информации пользователя
    public boolean USER(String user) throws IOException {
        controlSoket.sendMessage("USER " + user);
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.USER_SUCCESS);
    }

    //получение от клиента пароля пользователя
    public boolean PASS(String password) throws IOException {
        controlSoket.sendMessage("PASS " + password);
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.PASS_SUCCESS);
    }

    //отправка клиенту расширенной информации о списке файлов каталога
    public String LIST() throws IOException {

        PORT();

        dataSoket.setConnectMode(FtpDataSocket.RECV_LIST_MODE);
        dataSoket.start();

        controlSoket.sendMessage("LIST");
        controlSoket.recvReply();
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return null;

        controlSoket.recvReply();

        if (weitReplyCode(FtpProtocol.LIST_SUCCESS)) {
            return dataSoket.getListData();
        }

        return null;

    }

    //отправка клиенту сокращённой информации о списке файлов каталога
    public String NLST() throws IOException {

        PORT();

        dataSoket.setConnectMode(FtpDataSocket.RECV_LIST_MODE);
        dataSoket.start();

        controlSoket.sendMessage("NLST");
        controlSoket.recvReply();
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return null;

        controlSoket.recvReply();

        if (weitReplyCode(FtpProtocol.NLST_SUCCESS)) {
            return dataSoket.getListData();
        }

        return null;
    }

    //иимя текущего каталога
    public boolean PWD() throws IOException{
        controlSoket.sendMessage("PWD");
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.PWD_SUCCESS);
    }

    //смена текущего каталога сервера
    public boolean CWD(String dirName) throws IOException {
        controlSoket.sendMessage("CWD " + dirName);
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.CWD_SUCCESS);
    }

    //создание каталога
    public boolean MKD(String dirName) throws IOException {
        controlSoket.sendMessage("MKD " + dirName);
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.MKD_SUCCESS);
    }

    //удаление каталога
    public boolean RMD(String dirName) throws IOException {
        controlSoket.sendMessage("RMD " + dirName);
        controlSoket.recvReply();

        return weitReplyCode(FtpProtocol.RMD_SUCCESS);
    }

    //удаление файла на сервере
    public boolean DELE(String fileName) throws IOException {
        controlSoket.sendMessage("DELE " + fileName);
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.DELE_SUCCESS);
    }

    //получение параметров сокета клиента (адреса и порта), осуществляющего приём и передачу данных
    private boolean PORT() throws IOException {

        String[] address = Util.getLocalAddress().split("\\.");
        int port = dataSoket.getLocalPort();

        int p1 = (port & 0xFF00) >> 8;
        int p2 = port & 0x00FF;

        controlSoket.sendMessage("PORT " +
                Integer.valueOf(address[0]) + "," +
                Integer.valueOf(address[1]) + "," +
                Integer.valueOf(address[2]) + "," +
                Integer.valueOf(address[3]) + "," +
                p1 + "," +
                p2
        );

        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.PORT_SUCCESS);
    }

    //посылка файла клиенту
    public boolean RETR(File file) throws IOException {

        PORT();

        dataSoket.setFileData(file);
        dataSoket.setConnectMode(FtpDataSocket.RECV_FILE_MODE);
        dataSoket.start();

        controlSoket.sendMessage("RETR " + file.getAbsolutePath());
        controlSoket.recvReply();
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();

        if (!weitReplyCode(FtpProtocol.RETR_SUCCESS)) {
            file.delete();
        }
        return weitReplyCode(FtpProtocol.RETR_SUCCESS);
    }

    //запись полученного от клиента файла
    public boolean STOR(File file) throws IOException {

        if (!file.exists()){
            Util.log("File " + file.getAbsolutePath() + " not found.", Util.LOG_TYPE_ANSWER);
            return false;
        }

        PORT();

        dataSoket.setFileData(file);
        dataSoket.setConnectMode(FtpDataSocket.SEND_FILE_MODE);
        dataSoket.start();

        controlSoket.sendMessage("STOR " + file.getName());
        controlSoket.recvReply();
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();

        return weitReplyCode(FtpProtocol.STOR_SUCCESS);
    }

    //задание режима передачи данных
    // 0 - EBCDIC(E), 1 - ASCII(A), 2 - binary(I)
    public boolean TYPE() throws IOException {
        controlSoket.sendMessage("TYPE I");
        return weitReplyCode(FtpProtocol.TYPE_SUCCESS);
    }

    public boolean TYPE(int type) throws IOException {
        switch (type) {
            case 0:
                controlSoket.sendMessage("TYPE E");
                break;
            case 1:
                controlSoket.sendMessage("TYPE A");
                break;
            case 2:
                controlSoket.sendMessage("TYPE I");
                break;
            default:
                return false;
        }

        controlSoket.recvReply();

        return weitReplyCode(FtpProtocol.TYPE_SUCCESS);
    }

    //удаление всех помеченных сообщений и завершение сеанса
    public boolean QUIT() throws IOException {
        controlSoket.sendMessage("QUIT");
        controlSoket.recvReply();
        return weitReplyCode(FtpProtocol.QUIT_SUCCESS);
    }
}
