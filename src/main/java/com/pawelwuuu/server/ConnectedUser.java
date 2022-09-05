package com.pawelwuuu.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public ConnectedUser(String nick, Socket userSocket, DataOutputStream userOutput, DataInputStream userInput) {
        this.nick = nick;
        this.userSocket = userSocket;
        this.userOutput = userOutput;
        this.userInput = userInput;
    }

    void closeConnection() throws IOException {
        try {
            userSocket.close();
        } catch (IOException e){
            throw e;
        }
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
