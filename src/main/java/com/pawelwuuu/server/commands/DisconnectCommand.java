package com.pawelwuuu.server.commands;

import com.pawelwuuu.server.Server;

public class DisconnectCommand extends KickCommand{
    public DisconnectCommand(Server server, String nick) {
        super(server, nick);
        msg = " has disconnected.";
    }
}
