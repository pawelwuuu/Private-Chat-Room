package com.pawelwuuu.server.commands;

import com.pawelwuuu.Message;
import com.pawelwuuu.server.ConnectedUser;
import com.pawelwuuu.server.Server;

import java.io.IOException;
import java.util.Collection;

public class HelpCommand extends ConcreteCommand{
    ConnectedUser requestingUser;
    Collection<ConnectedUser> connectedUsers;

    public HelpCommand(Server server, Message message) {
        super(server);
        connectedUsers = server.getConnectedUsers();

        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(message.getSender())){
                requestingUser = connectedUser;
            }
        }
    }

    void showHelp(){
        String helpMsg = """
                Available commands are:
                /ban <nick> - prevents user with specified nick for connecting to the chat.
                /kick <nick> - kicks user with specified nick from the server.
                /list - displays users connected to chat.""";

        try {
            server.sendMessage(new Message(helpMsg, "Server"), requestingUser);
        } catch (IOException e) {
            requestingUser.closeConnection();
            connectedUsers.remove(requestingUser);
        }
    }

    @Override
    public void execute() {
        showHelp();
    }
}
