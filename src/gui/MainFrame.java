package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mmishak on 21/02/17.
 */
public class MainFrame extends JFrame {

    private JButton btnConnect = createButton("Connect");
    private JButton btnDisconnect = createButton("Disconnect");
    private JLabel lblUser = new JLabel("User:");
    private JLabel lblPassword = new JLabel("Password:");
    private JLabel lblServer = new JLabel("Server:");
    private JLabel lblFilesTitle = new JLabel("Files:");
    private JLabel lblLogTitle = new JLabel("Log:");
    private JTextField tfUser = createInputTextField();
    private JTextField tfPassword = createInputPasswordField();
    private JTextField tfServer = createInputTextField();
    private DefaultListModel<Object> listModelFiles = new DefaultListModel<>();
    private JTextArea textAreaLog = new JTextArea();

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        setMinimumSize(new Dimension(700, 500));

        setLayout(new BorderLayout());

        // ****************  Добавляем боковую панель  *****************************

        JPanel westPanel = new JPanel(new GridLayout(1, 1));
        add(westPanel, BorderLayout.WEST);

        JPanel connectInfoPanel = new JPanel();
        connectInfoPanel.setLayout(new BoxLayout(connectInfoPanel, BoxLayout.Y_AXIS));
        connectInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        connectInfoPanel.setPreferredSize(new Dimension(210, 500));
        westPanel.add(connectInfoPanel);

        JPanel serverInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 60);
        connectInfoPanel.add(serverInputPanel);

        JPanel userInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 50);
        connectInfoPanel.add(userInputPanel);

        JPanel passwordInputPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 80);
        connectInfoPanel.add(passwordInputPanel);

        JPanel buttonInputPanel = createPanel(new FlowLayout(FlowLayout.CENTER), 200, 100);
        connectInfoPanel.add(buttonInputPanel);

        serverInputPanel.add(lblServer);
        serverInputPanel.add(tfServer);

        userInputPanel.add(lblUser);
        userInputPanel.add(tfUser);

        passwordInputPanel.add(lblPassword);
        passwordInputPanel.add(tfPassword);

        buttonInputPanel.add(btnConnect);
        buttonInputPanel.add(btnDisconnect);

        btnDisconnect.setEnabled(false);

        // ****************  Добавляем центральную панель  *****************************

        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        add(centerPanel, BorderLayout.CENTER);
        centerPanel.setBackground(Color.BLACK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerPanel.add(mainPanel);


        JPanel titleFilesPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 25);
        JPanel titleLogPanel = createPanel(new FlowLayout(FlowLayout.LEADING), 200, 25);

        titleFilesPanel.add(lblFilesTitle);
        titleLogPanel.add(lblLogTitle);

        JList<Object> listFiles = new JList<>(listModelFiles);
        JScrollPane scrollFiles = new JScrollPane(listFiles);
        scrollFiles.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        textAreaLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(textAreaLog);
        scrollLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(titleFilesPanel);
        mainPanel.add(scrollFiles);
        mainPanel.add(titleLogPanel);
        mainPanel.add(scrollLog);

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

    private JPanel createPanel(LayoutManager layout, int x, int y) {
        JPanel panel = new JPanel(layout);
        panel.setMaximumSize(new Dimension(x, y));
        panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return panel;
    }
}
