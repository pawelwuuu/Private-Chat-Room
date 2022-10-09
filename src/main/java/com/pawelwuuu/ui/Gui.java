package com.pawelwuuu.ui;

import com.pawelwuuu.Exceptions.MessageFormatException;
import com.pawelwuuu.Message;
import com.pawelwuuu.client.Client;
import com.pawelwuuu.utils.ExternalIpChecker;
import com.pawelwuuu.utils.StringHash;
import com.pawelwuuu.server.Server;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Gui {
    private JPanel cardPanel;
    private JPanel lowerPanel;
    private JPanel upperPanel;
    private JRadioButton serverRadioButton;
    private JRadioButton clientRadioButton;
    private JTextArea messageBox;
    private JTextField messageInputField;
    private JButton sendMessageButton;
    private JTextField ipField;
    private JButton cancelButton;
    private JButton confirmButton;
    private JTextField passwordField;
    private JTextField nickField;
    private JTextArea errorTextArea;
    private JPanel settingsCard;
    private JPanel clientCard;
    private JLabel ipInformation;
    private JFrame frame;
    private Client client;
    private Server server;


    private final CardLayout cardLayout = (CardLayout)cardPanel.getLayout();

    public Gui(JFrame frame) {
        confirmButton.addActionListener(e -> {
            initChat();     //creating client and working server
        });

        cancelButton.addActionListener(e -> System.exit(0));
        sendMessageButton.addActionListener(e -> sendMessage(client, messageInputField));
        clientCard.registerKeyboardAction(e ->
                sendMessage(client, messageInputField),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        serverRadioButton.addActionListener(e -> ipField.setEnabled(false));
        clientRadioButton.addActionListener(e -> ipField.setEnabled(true));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.sendMessage(client.createMessage("/disconnect"));
                    System.exit(0);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Initiates both the server and client. Starts a programme.
     */
    void initChat(){
        String password = passwordField.getText();
        if (password.isBlank()){
            password = "DEFAULT";
        }

        String hashedPassword;
        try {
            hashedPassword = StringHash.hash(password);
        } catch (NoSuchAlgorithmException e) {
            errorTextArea.setText("Problem with password encoding has occurred.");
            return;
        }

        String nick = nickField.getText();

        try {
            if (serverRadioButton.isSelected()){
                InetAddress ip = InetAddress.getLocalHost();
                server = new Server(ip, hashedPassword);

                client = new Client(nick, password, ip.getHostAddress());
                server.init();

                cardLayout.show(cardPanel, "client");       //setting the client chat card visible

                ipInformation.setText("Your public ip is: " + ExternalIpChecker.getIp());
                ipInformation.setVisible(true);
            } else if (clientRadioButton.isSelected()){
                client = new Client(nick, password, ipField.getText());

                cardLayout.show(cardPanel, "client");       //setting the client chat card visible
            }

            new MessageBoxTask(messageBox).execute();           //receive messages
        } catch (Throwable e) {
            errorTextArea.setText(e.getMessage());

            server.shutdown();
            server = null;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Private Chat Room");
        frame.setContentPane(new Gui(frame).cardPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Sends a message to the server using certain client.
     * @param client Client object that will be used to send the message.
     * @param messageInputField JTextComponent that contains the message to send.
     */
    public void sendMessage(Client client, JTextComponent messageInputField){
        try{
            if ((messageInputField.getText().matches("/kick.+") || messageInputField.getText().matches("/ban.+"))
                    && ! serverRadioButton.isSelected()){

                JOptionPane.showMessageDialog(clientCard, "No permissions to use that command");
                return;
            }

            Message message = client.createMessage(messageInputField.getText());
            client.sendMessage(message);
            messageInputField.setText("");
        } catch (IOException ex){
            JOptionPane.showMessageDialog(clientCard, "Connection with server lost.");
        } catch (MessageFormatException ex) {
            JOptionPane.showMessageDialog(clientCard, ex.getMessage());
        }
    }

    /**
     * Updates status of message box by incoming messages.
     */
    class MessageBoxTask extends SwingWorker<String, String>{
        JTextArea messageBox;

        public MessageBoxTask(JTextArea messageBox) {
            this.messageBox = messageBox;
        }


        @Override
        protected String doInBackground() {
            while (true){
                try {
                    if (client.isInputAvailable()){
                        publish(client.receiveFormattedMessage());
                    }
                } catch (Throwable e){
                    e.printStackTrace();
//                    break; //todo enhance this
                }
            }

        }

        @Override
        protected void process(List<String> chunks){
            messageBox.append(chunks.get(0) + "\n");
        }
    }
}

