package logic;

import java.io.IOException;

/**
 * Created by mmishak on 28/02/17.
 */
public class FtpClient {

    private FtpProtocol ftp = new FtpProtocol();

    public FtpClient() throws IOException {
    }

    public boolean connect(String hostname, String user, String password) throws IOException {

        try {
            if (!ftp.connect(hostname, 21)) return false;
            if (!ftp.USER(user)) return false;
            if (!ftp.PASS(password)) return false;
            if (!ftp.PWD()) return false;
            if (!ftp.NLST()) return false;
            if (!ftp.CWD("tempDir")) return false;
            if (!ftp.STOR("Text.txt")) return false;
            if (!ftp.RETR("Text.txt")) return false;
            return true;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            ftp.QUIT();
            ftp.disconnect();
        }

        return true;
    }
}
