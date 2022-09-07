package com.pawelwuuu.server.commands;

import com.pawelwuuu.Message;
import com.pawelwuuu.server.ConnectedUser;
import com.pawelwuuu.server.Server;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListCommand extends ConcreteCommand{
    Collection<ConnectedUser> connectedUsers;
    ConnectedUser requestingUser;

    public ListCommand(Server server, String requestingUserNick) {
        super(server);
        connectedUsers = server.getConnectedUsers();

        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(requestingUserNick)){
                requestingUser = connectedUser;
            }
        }
    }

    void listUsers() {
        try {
            String nicks = "";
            for (ConnectedUser connectedUser: connectedUsers){
                nicks += connectedUser.getNick() + ", ";
            }

            server.sendMessage(new Message("connected users are: " + nicks, "Server"), requestingUser);
        } catch (IOException e) {
            requestingUser.closeConnection();
            connectedUsers.remove(requestingUser);
        }
    }

    @Override
    public void execute() {
        listUsers();
    }
}
