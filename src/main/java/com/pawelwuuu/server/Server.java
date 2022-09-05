package com.pawelwuuu.server;

import com.pawelwuuu.Message;

import javax.swing.*;

import static com.pawelwuuu.jsonUtil.JsonManager.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {
    private ServerSocket server;
    private final long serverTimestamp; //todo add feature
    private final int PORT = 43839;
//    private ConcurrentHashMap<Socket, DataOutputStream> outputStreams = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<Socket, DataInputStream> inputStreams = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<ConnectedUser> connectedUsers = new CopyOnWriteArrayList<>();
    private final String password;

    public Server(InetAddress serverIp, String password) {
        try{
            this.server = new ServerSocket(PORT, 100, serverIp);

        } catch (IOException e){
            System.out.println("Server creation failed because of IO problem.");
        } catch (IllegalArgumentException e){
            System.out.println("Application port is out of range.");
        } catch (Throwable e){
            System.out.println("Server construction failed, something gone wrong.");
        }

        this.serverTimestamp = Instant.EPOCH.getEpochSecond();
        this.password = password;
    }

     void establishConnection(){
        try{
            Socket clientSocket = server.accept();
            DataOutputStream outputSocket = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream inputSocket = new DataInputStream(clientSocket.getInputStream());

            String userNick = "";

            //TODO add encrypted password checking
            if (! password.equals("DEFAULT")) {
                String parsedPasswordMessage = receiveSocketMessage(inputSocket);
                Message passwordMessage = objectDeserialization(parsedPasswordMessage, Message.class);
                String receivedPassword = passwordMessage.getContent();

                userNick = passwordMessage.getSender();

                if (! receivedPassword.equals(password)) {
                    sendMessage(
                            new Message("Password is wrong, connection denied.", "Server"),
                            outputSocket);
                    clientSocket.close();
                    return;
                }
            }

//            outputStreams.putIfAbsent(clientSocket, outputSocket);
//            inputStreams.putIfAbsent(clientSocket, inputSocket);

            connectedUsers.add(
                    new ConnectedUser(userNick, clientSocket, outputSocket, inputSocket));
        } catch (IOException e){
            //pass
        }

    }

    void manageIncomingMessages(){
        while (true){
            for (ConnectedUser connectedUser: connectedUsers) {
                Socket clientSocket = connectedUser.getUserSocket();
                DataInputStream inputStream = connectedUser.getUserInput();

                try{
                    if (inputStream.available() > 0){
                        String message = receiveSocketMessage(inputStream);     //receiving message
                        if (objectDeserialization(message, Message.class).isContainingServerInformation()){
                            continue;                                           //checking if message doesn't contain information for server
                        }
                        broadcastMessage(message, clientSocket);    //broadcasting message
                    }
                } catch (IOException e){
                    try {
                        connectedUser.closeConnection();
                    } catch (IOException ex){
                        // pass
                    } finally {
                        connectedUsers.remove(connectedUser);
                    }
                }
            }

        }
    }

     public String receiveSocketMessage(DataInputStream socketInputStream) throws IOException{
        try{
            return socketInputStream.readUTF();
        } catch (IOException e){
            throw e;
        }

    }

    void broadcastMessage(String message){
        broadcastMessage(message, null);
    }

    void broadcastMessage(String message, Socket excludedSocket){
        connectedUsers.forEach(( connectedUser -> {
            try {
                Socket socket = connectedUser.getUserSocket();
                DataOutputStream dataOutputStream = connectedUser.getUserOutput();

                if (socket != excludedSocket){
                    dataOutputStream.writeUTF(message);
                }
            } catch (IOException e) {
                try {
                    connectedUser.closeConnection();
                } catch (IOException ex){
                    // pass
                } finally {
                    connectedUsers.remove(connectedUser);
                }
            }
        }));
    }

    public void establishConnections(){
        while (true){
            establishConnection();
        }
    }

    public void sendMessage(Message message, DataOutputStream outputStream) throws IOException {
        try {
            String parsedMessage = objectSerialization(message);
            outputStream.writeUTF(parsedMessage);
        } catch (IOException e){
            throw e;
        }
    }

    public void init(){
        System.out.println("Server Started!");
        System.out.println("Your public ip is: " + ExternalIpChecker.getIp() + "\n");

        Thread connectionManager = new Thread(() -> establishConnections());
        connectionManager.start();

        Thread messageManager = new Thread(() -> manageIncomingMessages());
        messageManager.start();
    }
}
