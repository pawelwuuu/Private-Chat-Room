package com.pawelwuuu.server.commands;

import com.pawelwuuu.Message;
import com.pawelwuuu.server.ConnectedUser;
import com.pawelwuuu.server.Server;

import java.util.Collection;

public class KickCommand extends ConcreteCommand{
    Collection<ConnectedUser> connectedUsers;
    String nick;

    public KickCommand(Server server, String nick) {
        super(server);
        this.connectedUsers = server.getConnectedUsers();
        this.nick = nick;
    }

    void kickUser(){
        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(nick)){
                connectedUser.closeConnection();

                connectedUsers.remove(connectedUser);
                server.broadcastMessage(new Message(connectedUser.getNick() + " has been kicked from the chat.", "Server"));
            }
        }
    }

    @Override
    public void execute() {
        kickUser();
    }
}
