package com.pawelwuuu.client;

import com.pawelwuuu.Exceptions.*;
import com.pawelwuuu.Message;
import com.pawelwuuu.Validator;
import com.pawelwuuu.utils.StringHash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import static com.pawelwuuu.utils.JsonManager.*;

/**
 * Represents a client side from client - server connection. It can connect to the server, send a message to the server
 * and receive a message from the server. Client by default works on port 43839. Represents a user by specified nickname.
 */
public class Client {
    private final String nick;
    private Socket socket;
    private boolean isOn = true;
    private DataOutputStream output;
    private DataInputStream input;
    private final int PORT = 43839;

    /**
     * Constructs a client object with specified nick, password to chat room and ip to connect to.
     * @param nick string containing nickname.
     * @param password string containing password to the server chat room.
     * @param serverIp string representation of ip. For instance 100.200.100.50.
     * @throws Throwable error that occurred during construction of the object.
     */
    public Client(String nick, String password, String serverIp) throws Throwable{
        try{
            if (! Validator.isNicknameCorrect(nick)){
                throw new InvalidNicknameException();
            }
            this.nick = nick;

            if (password != null &&  ! Validator.isPasswordCorrect(password)){
                throw new InvalidPasswordException();
            }

            if (serverIp != null && ! Validator.IsIpCorrect(serverIp)){
                throw new InvalidIpException();
            }
            //UNIQUE port of app
            this.socket = new Socket(serverIp, PORT);

            this.output = new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(socket.getInputStream());

            String hashedPassword;
            hashedPassword = StringHash.hash(password);

            sendMessage(new Message(hashedPassword, nick, true)); //sends message with password
        } catch (Throwable e){
            throw e;
        }
    }

    /**
     *
     */
    public void userInterface(){
        Thread receivingThread = new Thread(this::userReceivingMessageInterface);
        receivingThread.start();

        userSendingMessageInterface();
    }

    private void userSendingMessageInterface(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type exit to disconnect.");

        while (isOn){
            String userInput = scanner.nextLine();

            if (userInput.equals("exit")){
                isOn = false;
                return;
            }

            try {
                Message message = createMessage(userInput);
                sendMessage(message);
            } catch (IOException e){
                System.out.println("Error, connection withe server may be failed.");
                System.out.println("Reason: " + e.getMessage());
                System.out.println("Client is turning off.");
                isOn = false;
            } catch (Throwable e){
                System.out.println("Error: " + e.getMessage());
                System.out.println("Client is turning off.");
                isOn = false;
            }
        }
    }

    private void userReceivingMessageInterface() {
        while (isOn){
            try{
                if (input.available() > 0){
                    Message message = receiveMessage();

                    System.out.printf("%s: %s\n", message.getSender(), message.getContent());
                }
            } catch (IOException e){
                System.out.println("Connection with server failed.");
                System.out.println("Reason: " + e.getMessage());
                System.out.println("Client turning off.");
                isOn = false;
            } catch (Throwable e){
                System.out.println("Error: " + e.getMessage());
                System.out.println("Client turning off.");
                isOn = false;
            }
        }
    }

    public void sendMessage(Message message) throws IOException, MessageFormatException {
        if (message.getContent().isBlank()) {
            throw new MessageFormatException("Message cannot be blank or empty.");
        }
        String serializedMessage = objectSerialization(message);

        output.writeUTF(serializedMessage);
    }

    Message receiveMessage() throws IOException {
        try {
            String serializedMessage = input.readUTF();
            Message message = objectDeserialization(serializedMessage, Message.class);

            return message;
        } catch (Throwable e){
            throw e;
        }
    }

    public String receiveFormattedMessage() throws IOException{
        try {
            Message receivedMessage = receiveMessage();
            String formattedReceivedMsg = receivedMessage.getSender() + ": " + receivedMessage.getContent();
            return formattedReceivedMsg;
        } catch (Throwable e) {
            throw e;
        }
    }

    public boolean isInputAvailable() throws IOException {
        try{
            return input.available() > 0;
        } catch (IOException e){
            throw e;
        }
    }

    public Message createMessage(String content){
        if (content.startsWith("/")){
            return new Message(content, nick, true);
        }

        return new Message(content, nick);
    }
}
