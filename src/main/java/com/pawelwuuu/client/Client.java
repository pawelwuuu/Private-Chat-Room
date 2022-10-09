package com.pawelwuuu.client;

import com.pawelwuuu.Exceptions.*;
import com.pawelwuuu.Message;
import com.pawelwuuu.Validator;
import com.pawelwuuu.utils.StringHash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
    //PORT OF APPLICATION
    private final int PORT = 43839;

    /**
     * Constructs a client object with specified nick, password to chat room and ip to connect to. Password is being hashed
     * during construction. Also it connects to the server.
     * server.
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
            //CONNECTION TO THE SERVER
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
     * Sends message object to connected server via socket.
     * @param message message object.
     * @throws IOException thrown if problem with socket or connection has occurred.
     * @throws MessageFormatException thrown if message object contains message in invalid format.
     */
    public void sendMessage(Message message) throws IOException, MessageFormatException {
        if (message.getContent().isBlank()) {
            throw new MessageFormatException("Message cannot be blank or empty.");
        }
        String serializedMessage = objectSerialization(message);

        output.writeUTF(serializedMessage);
    }

    /**
     *Assumes that the incoming message is present, receives message from server socket.
     * @return Message object received from the server.
     * @throws IOException thrown if problem with socket or connection has occurred.
     */
    Message receiveMessage() throws IOException {
        try {
            String serializedMessage = input.readUTF();
            Message message = objectDeserialization(serializedMessage, Message.class);

            return message;
        } catch (Throwable e){
            throw e;
        }
    }

    /**
     * Assumes that the incoming message is present, receives message that is formatted. The format is present:
     * "Time | nick: message".
     * @return string with formatted message.
     * @throws IOException thrown if problem with socket or connection has occurred.
     */
    public String receiveFormattedMessage() throws IOException{
        try {
            Message receivedMessage = receiveMessage();
            String formattedReceivedMsg =
                    receivedMessage.getTimestamp() + " | " +
                    receivedMessage.getSender() + ": " + receivedMessage.getContent();

            return formattedReceivedMsg;
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * Determines if the incoming data from server is available.
     * @return true if incoming to socket input is available or false if it is not.
     * @throws IOException thrown if problem with socket or connection has occurred.
     */
    public boolean isInputAvailable() throws IOException {
        try{
            return input.available() > 0;
        } catch (IOException e){
            throw e;
        }
    }

    /**
     * Creates message object with specified content. Message object will consist of client nickname, and message content.
     * If message content starts with '/' character, then containingServerInformation flag will be set to true.
     * @param content string with message content.
     * @return message object.
     */
    public Message createMessage(String content){
        if (content.startsWith("/")){
            return new Message(content, nick, true);
        }

        return new Message(content, nick);
    }
}
