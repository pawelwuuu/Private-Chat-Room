package com.pawelwuuu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private ServerSocket server;
    private final long serverTimestamp;
    private final int PORT = 43839;
    private ConcurrentHashMap<Socket, DataOutputStream> outputStreams = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Socket, DataInputStream> inputStreams = new ConcurrentHashMap<>();
    private final String password;

    public Server(InetAddress externalIp, String password) {
        try{
            this.server = new ServerSocket(PORT, 100, externalIp);

        } catch (IOException e){
            System.out.println("com.pawelwuuu.Server creation failed because of IO problem.");
        } catch (IllegalArgumentException e){
            System.out.println("Application port is out of range.");
        } catch (Throwable e){
            System.out.println("com.pawelwuuu.Server construction failed, something gone wrong.");
        }

        this.serverTimestamp = Instant.EPOCH.getEpochSecond();
        this.password = password;
    }

    public Server(InetAddress externalIp){
        this(externalIp, null);
    }

     void establishConnection(){
        try{
            Socket clientSocket = server.accept();
            DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());


            outputStreams.putIfAbsent(clientSocket, clientOutput);
            inputStreams.putIfAbsent(clientSocket, clientInput);

        } catch (IOException e){
            // pass
        }

    }

    void manageIncomingMessages(){
        while (true){
            for (var entry: inputStreams.entrySet()) {
                Socket clientSocket = entry.getKey();
                DataInputStream inputStream = entry.getValue();

                try{
                    if (inputStream.available() > 0){
                        String message = receiveSocketMessage(inputStream);     //receiving message
                        broadcastMessage(message, clientSocket);    //broadcasting message
                    }
                } catch (IOException e){
                    inputStreams.remove(clientSocket);
                    outputStreams.remove(clientSocket);
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
        outputStreams.forEach(((socket, dataOutputStream) -> {
            try {
                if (socket != excludedSocket){
                    dataOutputStream.writeUTF(message);
                }
            } catch (IOException e) {
                inputStreams.remove(socket);
                outputStreams.remove(socket);
            }
        }));
    }

    public void establishConnections(){
        while (true){
            establishConnection();
        }
    }

    public void init(){
        System.out.println("Server Started!");
        System.out.println("");

        Thread connectionManager = new Thread(() -> establishConnections());
        connectionManager.start();

        manageIncomingMessages();
    }
}
