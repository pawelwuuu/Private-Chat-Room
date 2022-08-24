package com.pawelwuuu;

import com.pawelwuuu.Exceptions.InvalidIpException;
import com.pawelwuuu.Exceptions.InvalidNicknameException;
import com.pawelwuuu.Exceptions.InvalidPasswordException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;
import static com.pawelwuuu.jsonUtil.JsonManager.*;

public class Client {
    private String nick, password;
    private final int PORT = 43839;
    Socket socket;
    boolean isOn = true;
    DataOutputStream output;
    DataInputStream input;

    public Client(String nick, String password, String serverIp) {
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

            sendMessage(new Message(password, nick)); //sends message with password


        } catch (IOException e){
            System.out.println("Connection with server failed: " + e.getMessage());
            isOn = false;
        } catch (IllegalArgumentException e){
            System.out.println("Port value is wrong, probably out of range.");
            isOn = false;
        }
    }

//    public Client(String nick, String ServerIp) {
//        this(nick, null ,ServerIp);
//    }
//
//    public Client(String nick){
//        this(nick, null, "127.0.0.1");
//    }

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
                System.out.println("Client turning off.");
                isOn = false;
            } catch (Throwable e){
                System.out.println("Error: " + e.getMessage());
                System.out.println("Client turning off.");
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

    public Message createMessage(String content){
        return new Message(content, nick);
    }
}
