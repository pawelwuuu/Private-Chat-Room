package com.pawelwuuu.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Represents user connected to server. Consists of nick, socket, data outputs and inputs. It is possible to manage user
 * state by this class via methods providing closing connection.
 */
public class ConnectedUser {
    private String nick;
    private Socket userSocket;
    private DataOutputStream userOutput;
    private DataInputStream userInput;

    /**
     * Constructs the new ConnectedUser object. Creates new input and output for user socket.
     * @param nick string with user nick.
     * @param userSocket socket with connected client socket.
     * @throws IOException thrown when problem with socket or connection has occurred.
     */
    public ConnectedUser(String nick, Socket userSocket) throws IOException {
        this.nick = nick;
        this.userSocket = userSocket;

        try {
            this.userOutput = new DataOutputStream(userSocket.getOutputStream());
            this.userInput = new DataInputStream(userSocket.getInputStream());
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Constructs the new ConnectedUser object.
     * @param nick string with user nick.
     * @param userSocket socket with connected client socket.
     * @param userInput client socket input.
     * @param userOutput client socket output.
     * @throws IOException thrown when problem with socket or connection has occurred.
     */
    public ConnectedUser(String nick, Socket userSocket, DataOutputStream userOutput, DataInputStream userInput) {
        this.nick = nick;
        this.userSocket = userSocket;
        this.userOutput = userOutput;
        this.userInput = userInput;
    }

    /**
     * Closes the user socket.
     */
    public void closeConnection() {
        try {
            userSocket.close();
        } catch (IOException e){
            System.out.println("Cannot close connection with user.");
        }
    }

    public InetAddress getInetAddress(){
        return userSocket.getInetAddress();
    }

    public String getNick() {
        return nick;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public DataOutputStream getUserOutput() {
        return userOutput;
    }

    public DataInputStream getUserInput() {
        return userInput;
    }
}
