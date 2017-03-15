package logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mmishak on 28/02/17.
 */
public final class FtpClient {

    private static class FileInfo {
        private boolean isDir = false;
        private String name;

        public FileInfo(boolean isDir, String name) {
            this.isDir = isDir;
            this.name = name;
        }

        public boolean isDir() {
            return isDir;
        }

        public String getName() {
            return name;
        }
    }

    private static FtpProtocol ftp;
    private static ArrayList<FileInfo> fileList = new ArrayList<>();

    static {
        try {
            ftp = new FtpProtocol();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FtpClient() throws IOException {
    }

    public static boolean connect(String hostname, String user, String password) throws IOException {
        if (!ftp.connect(hostname, 21)) return false;
        if (!ftp.USER(user)) return false;
        if (!ftp.PASS(password)) return false;

        fileList = getFileList();

        return true;
    }

    public static boolean disconnect() throws IOException {
        ftp.QUIT();
        return ftp.disconnect();
    }

    private static ArrayList<FileInfo> getFileList() throws IOException {
        String answer1 = ftp.LIST();
        String answer2 = ftp.NLST();

        if (answer1 != null && answer2 != null) {
            ArrayList<String> arrayList1 = new ArrayList<String>(Arrays.asList(answer1.split("\n")));
            ArrayList<String> arrayList2 = new ArrayList<String>(Arrays.asList(answer2.split("\n")));

            fileList.clear();

            fileList.add(new FileInfo(true, "."));
            fileList.add(new FileInfo(true, ".."));
            for (int i = 0; i < arrayList1.size(); i++) {
                if (!arrayList1.get(i).equals(""))
                    fileList.add(new FileInfo(isDir(arrayList1.get(i)), arrayList2.get(i)));
            }
        }

        return fileList;
    }

    public static int getFileCount() {
        return fileList.size();
    }

    public static String getFileName(int fileIndex) {
        return fileList.get(fileIndex).getName();
    }

    public static boolean isDir(int fileIndex) {
        return fileList.get(fileIndex).isDir();
    }

    public static boolean storeFile(File file) throws IOException {
        boolean tmp = ftp.STOR(file);

        if (tmp)
            fileList = getFileList();

        return tmp;

    }

    public static boolean retrFile(int indexFileFirst, File fileSecond) throws IOException {
        boolean tmp = ftp.RETR(fileList.get(indexFileFirst).getName(), fileSecond);

        if (tmp)
            fileList = getFileList();

        return tmp;
    }

    public static boolean goToDir(int fileIndex) throws IOException {
        if (!isDir(fileIndex)) return false;

        boolean tmp = ftp.CWD(fileList.get(fileIndex).getName());

        if (tmp)
            fileList = getFileList();

        return tmp;
    }

    public static boolean makeDir(String dirName) throws IOException {
        boolean tmp = ftp.MKD(dirName);

        if (tmp)
            fileList = getFileList();

        return tmp;
    }

    public static boolean deleteFile(int fileIndex) throws IOException {
        boolean tmp = false;

        if (isDir(fileIndex)) {
            tmp = ftp.RMD(fileList.get(fileIndex).getName());
        } else {
            tmp = ftp.DELE(fileList.get(fileIndex).getName());
        }

        if (tmp)
            fileList = getFileList();

        return tmp;
    }

    private static boolean isDir(String fileName) {
        return fileName.charAt(0) == 'd';
    }

 }
