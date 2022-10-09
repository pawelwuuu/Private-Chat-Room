package com.pawelwuuu.server.commands;

import com.pawelwuuu.Message;
import com.pawelwuuu.server.ConnectedUser;
import com.pawelwuuu.server.Server;

import java.net.InetAddress;
import java.util.Collection;

public class BanCommand extends ConcreteCommand{
    String nick;
    Collection<ConnectedUser> connectedUsers;
    Collection<InetAddress> bannedUsers;

    public BanCommand(Server server, String nick) {
        super(server);
        this.nick = nick;
        this.connectedUsers = server.getConnectedUsers();
        this.bannedUsers = server.getBannedUsers();
    }

    void banUser(){
        for (ConnectedUser connectedUser: connectedUsers){
            if (connectedUser.getNick().equals(nick)){

                bannedUsers.add(connectedUser.getInetAddress());
                connectedUser.closeConnection();

                connectedUsers.remove(connectedUser);
                server.broadcastMessage(new Message(connectedUser.getNick() + " has been banned.", "Server"));
            }
        }
    }

    @Override
    public void execute() {
        banUser();
    }
}
