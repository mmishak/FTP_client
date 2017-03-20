package gui;

import logic.FtpClient;
import logic.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by mmishak on 21/02/17.
 */
public class MainFrame extends JFrame {

    private final JFileChooser fc = new JFileChooser();


    private JButton btnConnect = createButton("Connect");
    private JButton btnDisconnect = createButton("Disconnect");
    private JButton btnAddFile = createButton("Add file");
    private JButton btnCreateDir = createButton("Create directory");
    private JLabel lblUser = new JLabel("User:");
    private JLabel lblPassword = new JLabel("Password:");
    private JLabel lblServer = new JLabel("Server:");
    private JLabel lblFilesTitle = new JLabel("Files:");
    private JLabel lblLogTitle = new JLabel("Log:");
    private JLabel lblLocalAddress = new JLabel("Local address:");
    private JTextField tfUser = createInputTextField();
    private JTextField tfPassword = createInputPasswordField();
    private JTextField tfServer = createInputTextField();
    private JTextField tfLocalAddress = createInputTextField();
    private JList<Object> listFiles;
    private DefaultListModel<Object> listModelFiles = new DefaultListModel<>();
    private JTextArea textAreaLog = new JTextArea();
    private JMenuItem delete = new JMenuItem("Delete");
    private JMenuItem download = new JMenuItem("Download");

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 100, 700, 500);
        setMinimumSize(new Dimension(700, 500));

        setLayout(new BorderLayout());

        JPanel westPanel = createWestPanel();
        JPanel centerPanel = createCenterPanel();

        createActionListeners();

        Util.setLogArea(textAreaLog);

        setDefaultLogin();
    }

    private void setDefaultLogin() {                // TEMP!!!!!! ******************************
        tfLocalAddress.setText("127.0.0.1");
        tfUser.setText("mmishak");
        tfPassword.setText("mmpass");
        tfServer.setText("127.0.0.1");
    }

    private JPanel createWestPanel() {

        JPanel tmpPanel = new JPanel(new GridLayout(1, 1));
        add(tmpPanel, BorderLayout.WEST);

        JPanel connectInfoPanel = createConnectInfoPanel();

        tmpPanel.add(connectInfoPanel);

        return tmpPanel;
    }

    private JPanel createCenterPanel() {
        JPanel tmpPanel = new JPanel(new GridLayout(1, 1));
        add(tmpPanel, BorderLayout.CENTER);
        tmpPanel.setBackground(Color.BLACK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tmpPanel.add(mainPanel);


        JPanel titleFilesPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 600, 25);
        JPanel titleLogPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 600, 25);
        JPanel buttonsPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 500, 25);

        titleFilesPanel.add(lblFilesTitle);
        titleLogPanel.add(lblLogTitle);

        btnAddFile.setEnabled(false);
        btnCreateDir.setEnabled(false);

        buttonsPanel.add(btnAddFile);
        buttonsPanel.add(btnCreateDir);

        listFiles = new JList<>(listModelFiles);
        JScrollPane scrollFiles = new JScrollPane(listFiles);
        scrollFiles.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        textAreaLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(textAreaLog);
        scrollLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(titleFilesPanel);
        mainPanel.add(scrollFiles);
        mainPanel.add(buttonsPanel);
        mainPanel.add(titleLogPanel);
        mainPanel.add(scrollLog);

        return tmpPanel;
    }

    private JPanel createConnectInfoPanel() {
        JPanel tmpPanel = new JPanel();
        tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.Y_AXIS));
        tmpPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tmpPanel.setPreferredSize(new Dimension(210, 500));


        JPanel serverInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 60);
        tmpPanel.add(serverInputPanel);

        JPanel userInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 50);
        tmpPanel.add(userInputPanel);

        JPanel passwordInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 80);
        tmpPanel.add(passwordInputPanel);

        JPanel localAddressInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 80);
        tmpPanel.add(localAddressInputPanel);

        JPanel buttonInputPanel = createPanel(new FlowLayout(FlowLayout.CENTER), 200, 100);
        tmpPanel.add(buttonInputPanel);

        serverInputPanel.add(lblServer);
        serverInputPanel.add(tfServer);

        userInputPanel.add(lblUser);
        userInputPanel.add(tfUser);

        passwordInputPanel.add(lblPassword);
        passwordInputPanel.add(tfPassword);

        localAddressInputPanel.add(lblLocalAddress);
        localAddressInputPanel.add(tfLocalAddress);

        buttonInputPanel.add(btnConnect);
        buttonInputPanel.add(btnDisconnect);

        btnDisconnect.setEnabled(false);

        return tmpPanel;
    }

    private JPanel createPanel(LayoutManager layout, int x, int y) {
        JPanel panel = new JPanel(layout);
        panel.setMaximumSize(new Dimension(x, y));
        panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return panel;
    }

    private JButton createButton(String caption) {
        JButton button = new JButton(caption);
        button.setPreferredSize(new Dimension(200, 30));
        return button;
    }

    private JTextField createInputTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(190, 25));
        textField.setHorizontalAlignment(JTextField.CENTER);
        return textField;
    }

    private JTextField createInputPasswordField() {
        JPasswordField textField = new JPasswordField();
        textField.setPreferredSize(new Dimension(190, 25));
        textField.setHorizontalAlignment(JPasswordField.CENTER);
        return textField;
    }

    private void createActionListeners() {
        listFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFiles.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (FtpClient.goToDir(index)) {
                                    updateFileList();
                                }
                            } catch (IOException e) {
                                showErrorMessage("Ошибка");
                            }
                        }
                    }).start();

                }
            }
        });


        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!tfLocalAddress.getText().equals(""))
                                Util.setLocalAddress(tfLocalAddress.getText());
                            else
                                tfLocalAddress.setText(Util.getLocalAddress());

                            btnConnect.setEnabled(false);
                            tfServer.setEnabled(false);
                            tfUser.setEnabled(false);
                            tfPassword.setEnabled(false);
                            tfLocalAddress.setEnabled(false);

                            if (FtpClient.connect(tfServer.getText(), tfUser.getText(), tfPassword.getText())) {
                                btnAddFile.setEnabled(true);
                                btnDisconnect.setEnabled(true);
                                btnCreateDir.setEnabled(true);
                                updateFileList();
                            } else
                                throw new IOException();
                        } catch (IOException e1) {
                            showErrorMessage("Ошибка подключения");
                            btnConnect.setEnabled(true);
                            tfServer.setEnabled(true);
                            tfUser.setEnabled(true);
                            tfPassword.setEnabled(true);
                            tfLocalAddress.setEnabled(true);
                        }
                    }
                }).start();

            }
        });

        btnDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (FtpClient.disconnect()) {
                                btnConnect.setEnabled(true);
                                btnDisconnect.setEnabled(false);
                                tfServer.setEnabled(true);
                                tfUser.setEnabled(true);
                                tfPassword.setEnabled(true);
                                tfLocalAddress.setEnabled(true);
                                listModelFiles.clear();
                                btnAddFile.setEnabled(false);
                                btnCreateDir.setEnabled(false);
                            } else
                                throw new IOException();
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Неизвестная ошибка",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });

        btnCreateDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String dirName = showInputDialog("Create directory", "Directory name:");
                            if (dirName == null) return;
                            if (FtpClient.makeDir(dirName)) {
                                updateFileList();
                            } else
                                throw new IOException();
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Неизвестная ошибка",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });

        btnAddFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            File file = getAddFile();

                            if (file == null) return;

                            if (FtpClient.storeFile(file)) {
                                updateFileList();
                                showSuccessLoadMessage("Загрузка завершена");
                            } else
                                throw new IOException();
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Неизвестная ошибка",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });

        listFiles.addMouseListener(new MouseListener() {
            private void check(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    listFiles.setSelectedIndex(listFiles.locationToIndex(e.getPoint()));
                    JPopupMenu jpm = createPopupMenu(listFiles.getSelectedIndex());

                    if (jpm != null)
                        jpm.show(listFiles, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                check(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (FtpClient.deleteFile(listFiles.getSelectedIndex())) {
                                updateFileList();
                            } else
                                throw new IOException();
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Неизвестная ошибка",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            File file = getSaveFile(FtpClient.getFileName(listFiles.getSelectedIndex()));

                            if (file == null){
                                throw new IOException();
                            }
                            if (FtpClient.retrFile(listFiles.getSelectedIndex(), file)) {
                                updateFileList();
                                showSuccessLoadMessage("Файл загружен");
                            } else {
                                file.delete();
                                throw new IOException();
                            }
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Неизвестная ошибка",
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });
    }

    private JPopupMenu createPopupMenu(int index) {
        if (index < 2) return null;         // выбрана текущаяя или родительская директория ("." или "..")

        JPopupMenu jpm = new JPopupMenu();
        jpm.add(delete);
        if (!FtpClient.isDir(index))
            jpm.add(download);

        return jpm;
    }

    private File getSaveFile(String fileName) throws IOException {

        fc.setSelectedFile(new File(fileName));

        int returnVal = fc.showSaveDialog(MainFrame.this);
        File tmp = null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            tmp = fc.getSelectedFile();
            if (tmp.exists()) {
                showErrorMessage("Файл уже существует");
                return null;
            } else {
                tmp.createNewFile();
            }
        }

        return tmp;
    }

    private File getAddFile() {
        fc.setSelectedFile(new File(""));
        int returnVal = fc.showOpenDialog(MainFrame.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    private void showErrorMessage(String string) {
        JOptionPane.showMessageDialog(MainFrame.this,
                string,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessLoadMessage(String string) {
        JOptionPane.showMessageDialog(MainFrame.this,
                string,
                "Все норм",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String showInputDialog(String title, String message) {
        Object tmp = JOptionPane.showInputDialog(MainFrame.this,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
        return tmp != null ? (String) tmp : null;
    }

    private void updateFileList() {
        listModelFiles.clear();
        for (int i = 0; i < FtpClient.getFileCount(); i++) {
            if (FtpClient.isDir(i))
                listModelFiles.addElement("--[D]--     " + FtpClient.getFileName(i));
            else
                listModelFiles.addElement("--[   ]--     " + FtpClient.getFileName(i));
        }

    }


}
