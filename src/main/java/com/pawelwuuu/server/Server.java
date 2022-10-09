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
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {
    private ServerSocket server;
    //APPLICATION PORT.
    private final int PORT = 43839;
    Thread connectionManager;
    Thread messageManager;
    volatile boolean isOn; //VOLATILE FIELD FOR SAFETY.

    private CopyOnWriteArrayList<ConnectedUser> connectedUsers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<InetAddress> bannedUsers = new CopyOnWriteArrayList<>();
    private final String password;

    /**
     * Constructs the server object with passed server ip and password to the chat room. Changes isOn flag to true. If
     * creation of server gone wrong, it is throwing out exception.
     * @param serverIp InetAddress with server ip.
     * @param password string password to the server.
     * @throws IOException thrown when problem with socket or connection has occurred.
     */
    public Server(InetAddress serverIp, String password) throws IOException {
        try {
            this.server = new ServerSocket(PORT, 100, serverIp);

        } catch (Throwable e){
            throw e;
        }

        this.password = password;
        this.isOn = true;
    }

    /**
     *Blocks till new connection request appear. Creates new input and output for socket that want to connect. Checks
     * whether ip of that socket is on banned list, nick of client is already in use or password is invalid,
     * if so disconnects that socket. If socket is approved to connect, then it is used to creation of ConnectedUser
     * object which is added to connectedUsers collection.
     */
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

    /**
     * This method works until isOn flag is set to true. Checks if new messages is available, if so it is checking message
     * for containing of server information that may be password to chat room or command to execute. If the incoming
     * message contains command that is in correct form, it executes it, otherwise it sends message to the client with
     * information about unknown command used by it. If received message does not contain server information then it
     * broadcasts the message to other connected sockets. If there is a problem while sending a message to certain socket,
     * it disconnects it. If excludeSendingSocket parameter is set to true, then method does not send message to the
     * socket that is the author of it.
     * @param excludeSendingSocket boolean that determines policy of sending messages to it's author socket.
     */
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

    /**
     * Sends message to all the connected clients. If excludeSendingSocket parameter is set to true,
     * then method does not send message to the socket that is the author of it.
     * @param excludedSocket boolean that determines policy of sending messages to it's author socket.
     * @param parsedMessage string with serialized message object.
     */
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

    /**
     * Receives message from server side client socket input.
     * @param socketInputStream DataInputStream object which is socket input stream.
     * @return string with serialized received message.
     * @throws IOException thrown when problem with socket or connection has occurred.
     */
     public String receiveSocketMessage(DataInputStream socketInputStream) throws IOException{
        try{
            return socketInputStream.readUTF();
        } catch (Throwable e){
            throw e;
        }

    }

    /**
     * This method launches establishConnection method in while loop until isOn flag is true.
     */
    public void establishConnections(){
        while (isOn){
            establishConnection();
        }
    }

    /**
     * Serializes message object and sends it to certain server side client output stream.
     * @param message message object.
     * @param outputStream certain server side client output stream.
     * @throws IOException thrown when problem with socket or connection has occurred.
     */
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

    /**
     * Initiates the server. Starts managing incoming connections and messages.
     */
    public void init(){
        System.out.println("Server Started!");

        connectionManager = new Thread(() -> establishConnections());
        connectionManager.start();

//        if (isGui){
            messageManager = new Thread(() -> manageIncomingMessages(false));
//        } else {
//            messageManager = new Thread(() -> manageIncomingMessages(true));
//        }
        messageManager.start();
    }

    /**
     * Determines if the passed nick is already in use by other client.
     * @param nick string with nick.
     * @return true if nick is already in use, otherwise false.
     */
    public boolean isNickUsed(String nick){
        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(nick)){
                return true;
            }
        }

        return false;
    }

    /**
     * Shuts down the server.
     */
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
