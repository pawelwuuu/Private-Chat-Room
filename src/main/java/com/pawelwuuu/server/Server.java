package com.pawelwuuu.server;

import com.pawelwuuu.Exceptions.UnknownCommandException;
import com.pawelwuuu.Message;
import static com.pawelwuuu.utils.JsonManager.*;

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
    private final int PORT = 43839;
    Thread connectionManager;
    Thread messageManager;
    volatile boolean isOn;

    private CopyOnWriteArrayList<ConnectedUser> connectedUsers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<InetAddress> bannedUsers = new CopyOnWriteArrayList<>();
    private final String password;

    public Server(InetAddress serverIp, String password) throws IOException {
        try {
            this.server = new ServerSocket(PORT, 100, serverIp);

        } catch (Throwable e){
            throw e;
        }

        this.password = password;
        this.isOn = true;
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

            if (bannedUsers.contains(clientSocket.getInetAddress())){
                sendMessage(
                        new Message("You are banned from this server, connection denied.", "Server"),
                        outputSocket);
                clientSocket.close();
                return;
            }

            //TODO add encrypted password checking
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

            connectedUsers.add(
                    new ConnectedUser(userNick, clientSocket, outputSocket, inputSocket));
            broadcastMessage(new Message(userNick + " connected to the chat.", "Server"));
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

     void manageIncomingMessages(boolean excludeSendingSocket){
        while (isOn){
            for (ConnectedUser connectedUser: connectedUsers) {
                Socket clientSocket = connectedUser.getUserSocket();
                DataInputStream inputStream = connectedUser.getUserInput();

                try{
                    if (inputStream.available() > 0){
                        String parsedMessage = receiveSocketMessage(inputStream);     //receiving message
                        Message message = objectDeserialization(parsedMessage, Message.class);
                        if (message.isContainingServerInformation()){           //checking if message doesn't contain information for server
                            try {
                                CommandExecutor.executeCommand(message, this);
                            } catch (UnknownCommandException e) {
                                sendMessage(new Message(e.getMessage() + " Use /help command.", "Server"), connectedUser);
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
                    synchronized (this) {
                        if (! connectedUsers.contains(connectedUser)){
                            continue;
                        }

                        connectedUser.closeConnection();

                        String msg = connectedUser.getNick() + " has disconnected.";
                        connectedUsers.remove(connectedUser);
                        broadcastMessage(new Message(msg, "Server"));
                    }
                } catch (Throwable e){
                    e.printStackTrace();
                }
            }

        }
    }

    public void broadcastMessage(String parsedMessage, Socket excludedSocket){
        for (ConnectedUser connectedUser: connectedUsers) {
            try {
                Socket socket = connectedUser.getUserSocket();
                DataOutputStream dataOutputStream = connectedUser.getUserOutput();

                if (socket != excludedSocket) {
                    dataOutputStream.writeUTF(parsedMessage);
                }
            } catch (IOException e) {
                synchronized (this) {
                    if (! connectedUsers.contains(connectedUser)){
                        continue;
                    }

                    connectedUser.closeConnection();

                    String msg = connectedUser.getNick() + " has disconnected.";
                    connectedUsers.remove(connectedUser);
                    broadcastMessage(new Message(msg, "Server", false));
                    System.out.println(msg);

                }
            } catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(Message message){
        broadcastMessage(objectSerialization(message), null);
    }


    void broadcastMessage(String parsedMessage){
        broadcastMessage(parsedMessage, null);
    }

     public String receiveSocketMessage(DataInputStream socketInputStream) throws IOException{
        try{
            return socketInputStream.readUTF();
        } catch (Throwable e){
            throw e;
        }

    }

    public void establishConnections(){
        while (isOn){
            establishConnection();
        }
    }

    public void sendMessage(Message message, DataOutputStream outputStream) throws IOException {
        try {
            String parsedMessage = objectSerialization(message);
            outputStream.writeUTF(parsedMessage);
        } catch (Throwable e){
            throw e;
        }
    }
    public void sendMessage(Message message, ConnectedUser connectedUser) throws IOException {
        try {
            sendMessage(message, connectedUser.getUserOutput());
        } catch (Throwable e){
            throw e;
        }
    }

    public void init(boolean isGui){
        System.out.println("Server Started!");

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
            isOn = false;
            server = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public CopyOnWriteArrayList<ConnectedUser> getConnectedUsers() {
        return connectedUsers;
    }

    public CopyOnWriteArrayList<InetAddress> getBannedUsers() {
        return bannedUsers;
    }
}
