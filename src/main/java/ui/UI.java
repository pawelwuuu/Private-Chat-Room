package ui;

import com.pawelwuuu.client.Client;
import com.pawelwuuu.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class UI {
    private JPanel cardPanel;
    private JPanel lowerPanel;
    private JPanel upperPanel;
    private JRadioButton serverRadioButton;
    private JRadioButton clientRadioButton;
    private JTextArea messageBox;
    private JTextField textField1;
    private JButton button1;
    private JTextField ipField;
    private JButton cancelButton;
    private JButton confirmButton;
    private JTextField passwordField;
    private JTextField nickField;
    private JLabel errorLabel;
    private JPanel settingsCard;
    private JPanel clientCard;
    private Client client;
    private Server server;

    private CardLayout cardLayout = (CardLayout)cardPanel.getLayout();

    public UI() {

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initChat();
//                Thread receiveMsg = new Thread(() -> printMessages());
//                receiveMsg.start();

                new MessageBoxTask(messageBox).execute();
                cardLayout.show(cardPanel, "client");       //setting the client chat card visible

            }
        });
    }


    void initChat(){
        String password = passwordField.getText();
        String nick = nickField.getText();

        if (serverRadioButton.isSelected()){
            try {
                InetAddress ip = InetAddress.getLocalHost();

                server = new Server(ip, password);
                server.init();

                client = new Client(nick, password, ip.getHostAddress());
            } catch (Throwable ex){
                errorLabel.setText(ex.getMessage());
            }
        } else {
            try {
                client = new Client(nick, password, ipField.getText());
            } catch (Throwable ex){
                errorLabel.setText(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Private Chat Room");
        frame.setContentPane(new UI().cardPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void printMessages(){
        while (true){
            try {
                if (client.isInputAvailable()){
                    messageBox.append(client.receiveFormattedMessage() + "\n");
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class MessageBoxTask extends SwingWorker<String, String>{
        JTextArea messageBox;

        public MessageBoxTask(JTextArea messageBox) {
            this.messageBox = messageBox;
        }

        @Override
        protected String doInBackground() throws Exception {
            while (true){
                try {
                    if (client.isInputAvailable()){
                        publish(client.receiveFormattedMessage());
                    }
                } catch (IOException e){
                    e.printStackTrace();
//                    break; //todo enhance this
                }
            }

//            return null;    //shouldn't end
        }

        @Override
        protected void process(List<String> chunks){
            messageBox.append(chunks.get(0) + "\n");
        }
    }
}

