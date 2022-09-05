package com.pawelwuuu.client;

import com.pawelwuuu.Exceptions.InvalidIpException;
import com.pawelwuuu.Exceptions.InvalidNicknameException;
import com.pawelwuuu.Exceptions.InvalidPasswordException;
import com.pawelwuuu.Exceptions.ValidatorException;
import com.pawelwuuu.Message;
import com.pawelwuuu.Validator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLOutput;
import java.time.Instant;
import java.util.Scanner;
import static com.pawelwuuu.jsonUtil.JsonManager.*;

public class Client {
    private String nick, password;
    private final int PORT = 43839;     //UNIQUE port of app
    Socket socket;
    boolean isOn = true;
    DataOutputStream output;
    DataInputStream input;

    public Client(String nick, String password, String serverIp) throws Throwable{
        try{
            if (! Validator.isNicknameCorrect(nick)){
                throw new InvalidNicknameException();
            }
            this.nick = nick;

            if (password != null &&  ! Validator.isPasswordCorrect(password)){
                throw new InvalidPasswordException();
            }
            this.password = password;

            if (serverIp != null && ! Validator.IsIpCorrect(serverIp)){
                throw new InvalidIpException();
            }
            this.socket = new Socket(serverIp, PORT);

            this.output = new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(socket.getInputStream());

            sendMessage(new Message(password, nick, true)); //sends message with password
        } catch (Throwable e){
            throw e;
        }
    }

    public void userInterface(){
        Thread receivingThread = new Thread(() -> userReceivingMessageInterface());
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

    public void sendMessage(Message message) throws IOException {
        try {
            String serializedMessage = objectSerialization(message);

            output.writeUTF(serializedMessage);
        } catch (IOException e){
            throw e;
        }
    }

    Message receiveMessage() throws IOException {
        try {
            String serializedMessage = input.readUTF();
            Message message = objectDeserialization(serializedMessage, Message.class);

            return message;
        } catch (IOException e){
            throw e;
        }
    }

    public String receiveFormattedMessage() throws IOException{
        try {
            Message receivedMessage = receiveMessage();
            String formattedReceivedMsg = receivedMessage.getSender() + ": " + receivedMessage.getContent();
            return formattedReceivedMsg;
        } catch (IOException e) {
            throw e;
        }
    }

    public boolean isInputAvailable() throws IOException {
        try{
            if (input.available() > 0){
                return true;
            }

            return false;
        } catch (IOException e){
            throw e;
        }
    }

    public Message createMessage(String content){
        return new Message(content, nick);
    }
}
