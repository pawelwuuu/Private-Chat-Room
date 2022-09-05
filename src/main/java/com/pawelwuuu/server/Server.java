package com.pawelwuuu.server;

import com.pawelwuuu.Message;
import static com.pawelwuuu.jsonUtil.JsonManager.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {
    private ServerSocket server;
    private final long serverTimestamp; //todo add feature
    private final int PORT = 43839;
    Thread connectionManager;
    Thread messageManager;

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

            String parsedPasswordMessage = receiveSocketMessage(inputSocket);
            Message passwordMessage = objectDeserialization(parsedPasswordMessage, Message.class);
            String receivedPassword = passwordMessage.getContent();

            String userNick = passwordMessage.getSender();

            //TODO add encrypted password checking
            if (! password.equals("DEFAULT")) {
                if (! receivedPassword.equals(password)) {
                    sendMessage(
                            new Message("Password is wrong, connection denied.", "Server"),
                            outputSocket);
                    clientSocket.close();
                    return;
                }

                if (isNickUsed(userNick)){
                    sendMessage(
                            new Message("Nickname already in use, connection denied.", "Server"),
                            outputSocket);
                    clientSocket.close();
                    return;
                }
            }

            connectedUsers.add(
                    new ConnectedUser(userNick, clientSocket, outputSocket, inputSocket));
        } catch (IOException e){
            //pass
        }

    }

    void manageIncomingMessages(boolean excludeSendingSocket){
        while (true){
            for (ConnectedUser connectedUser: connectedUsers) {
                Socket clientSocket = connectedUser.getUserSocket();
                DataInputStream inputStream = connectedUser.getUserInput();

                try{
                    if (inputStream.available() > 0){
                        String parsedMessage = receiveSocketMessage(inputStream);     //receiving message
                        Message message = objectDeserialization(parsedMessage, Message.class);
                        if (message.isContainingServerInformation()){           //checking if message doesn't contain information for server
                            if (message.getContent().matches("/kick.+")){
                               kickUser(message.getContent().substring(6));
                            }

                            continue;
                        }

                        if (excludeSendingSocket) {
                            broadcastMessage(parsedMessage, clientSocket);    //broadcasting message
                        } else {
                            broadcastMessage(parsedMessage);
                        }
                    }
                } catch (IOException e){
                    connectedUser.closeConnection();
                    connectedUsers.remove(connectedUser);
                };
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
                connectedUser.closeConnection();
                connectedUsers.remove(connectedUser);
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

    public void init(boolean isGui){
        System.out.println("Server Started!");
        System.out.println("Your public ip is: " + ExternalIpChecker.getIp() + "\n");

        connectionManager = new Thread(() -> establishConnections());
        connectionManager.start();

        if (isGui){
            messageManager = new Thread(() -> manageIncomingMessages(false));
        } else {
            messageManager = new Thread(() -> manageIncomingMessages(true));
        }

        messageManager.start();
    }

    public boolean isNickUsed(String nick){
        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(nick)){
                return true;
            }
        }

        return false;
    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            messageManager.interrupt();
            messageManager = null;

            connectionManager.interrupt();
            connectionManager = null;

            server = null;
        }
    }

    void kickUser(String nick){
        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(nick)){
                connectedUser.closeConnection();

                connectedUsers.remove(connectedUser);
            }
        }
    }
}
