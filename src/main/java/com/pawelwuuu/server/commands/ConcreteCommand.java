package com.pawelwuuu.server.commands;

import com.pawelwuuu.server.Command;
import com.pawelwuuu.server.Server;

public abstract class ConcreteCommand implements Command {
    Server server;

    public ConcreteCommand(Server server) {
        this.server = server;
    }
}
