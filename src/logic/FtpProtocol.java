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
        dataSoket = new FtpDataSocket(53797);
    }

    private boolean weitReplyCode(int code) {
        return controlSoket.getReplyCode() == code;
    }

    public boolean connect(String hostname, int port) {
        try {

            System.out.println("--> Try connect to " + hostname);

            controlSoket = new FtpControlSocket(hostname, port);

            System.out.println(controlSoket.getReply());
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
            System.out.println("--> Socket closed");
            return true;
        } catch (IOException e) {
            System.out.println("--> Socket not closed");
            return false;
        }

    }

    //получение от клиента идентификационной информации пользователя
    public boolean USER(String user) throws IOException {
        controlSoket.sendMessage("USER " + user);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.USER_SUCCESS);
    }

    //получение от клиента пароля пользователя
    public boolean PASS(String password) throws IOException {
        controlSoket.sendMessage("PASS " + password);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.PASS_SUCCESS);
    }

    //отправка клиенту расширенной информации о списке файлов каталога
    public boolean LIST() throws IOException {

        PORT();

        dataSoket.setConnectMode(FtpDataSocket.RECV_LIST_MODE);
        dataSoket.start();

        controlSoket.sendMessage("LIST");
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());

        if (weitReplyCode(FtpProtocol.LIST_SUCCESS)) {
            System.out.println(dataSoket.getListData());
        }

        return weitReplyCode(FtpProtocol.LIST_SUCCESS);

    }

    //отправка клиенту сокращённой информации о списке файлов каталога
    public boolean NLST() throws IOException {

        PORT();

        dataSoket.setConnectMode(FtpDataSocket.RECV_LIST_MODE);
        dataSoket.start();

        controlSoket.sendMessage("NLST");
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());

        if (weitReplyCode(FtpProtocol.NLST_SUCCESS)) {
            System.out.println(dataSoket.getListData());
        }

        return weitReplyCode(FtpProtocol.NLST_SUCCESS);
    }

    //иимя текущего каталога
    public boolean PWD() throws IOException{
        controlSoket.sendMessage("PWD");
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.PWD_SUCCESS);
    }

    //смена текущего каталога сервера
    public boolean CWD(String dirName) throws IOException {
        controlSoket.sendMessage("CWD " + dirName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.CWD_SUCCESS);
    }

    //создание каталога
    public boolean MKD(String dirName) throws IOException {
        controlSoket.sendMessage("MKD " + dirName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.MKD_SUCCESS);
    }

    //удаление каталога
    public boolean RMD(String dirName) throws IOException {
        controlSoket.sendMessage("RMD " + dirName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.RMD_SUCCESS);
    }

    //удаление файла на сервере
    public boolean DELE(String fileName) throws IOException {
        controlSoket.sendMessage("DELE " + fileName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
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
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.PORT_SUCCESS);
    }

    //посылка файла клиенту
    public boolean RETR(String fileName) throws IOException {

        File tempFile = new File(fileName);
        if (!tempFile.exists())
            tempFile.createNewFile();

        PORT();

        dataSoket.setFileData(tempFile);
        dataSoket.setConnectMode(FtpDataSocket.RECV_FILE_MODE);
        dataSoket.start();

        controlSoket.sendMessage("RETR " + fileName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());

        if (!weitReplyCode(FtpProtocol.RETR_SUCCESS)) {
            tempFile.delete();
        }
        return weitReplyCode(FtpProtocol.RETR_SUCCESS);
    }

    //запись полученного от клиента файла
    public boolean STOR(String fileName) throws IOException {
        File tempFile = new File(fileName);

        if (!tempFile.exists()){
            System.out.println("File " + fileName + "not found.");
            return false;
        }

        PORT();

        dataSoket.setFileData(tempFile);
        dataSoket.setConnectMode(FtpDataSocket.SEND_FILE_MODE);
        dataSoket.start();

        controlSoket.sendMessage("STOR " + fileName);
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        if (!weitReplyCode(FtpProtocol.DATA_CONNECT_SUCCESS)) return false;

        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());

        return weitReplyCode(FtpProtocol.STOR_SUCCESS);
    }

    //задание режима передачи данных
    // 0 - EBCDIC(E), 1 - ASCII(A), 2 - binary(I)
    public boolean TYPE() throws IOException {
        controlSoket.sendMessage("TYPE I");
        System.out.println(controlSoket.getReply());
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
        System.out.println(controlSoket.getReply());

        return weitReplyCode(FtpProtocol.TYPE_SUCCESS);
    }

    //удаление всех помеченных сообщений и завершение сеанса
    public boolean QUIT() throws IOException {
        controlSoket.sendMessage("QUIT");
        controlSoket.recvReply();
        System.out.println(controlSoket.getReply());
        return weitReplyCode(FtpProtocol.QUIT_SUCCESS);
    }
}
