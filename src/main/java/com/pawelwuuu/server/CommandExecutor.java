package com.pawelwuuu.server;

import com.pawelwuuu.Exceptions.UnknownCommandException;
import com.pawelwuuu.Message;
import com.pawelwuuu.server.commands.*;

/**
 * This class is responsible for executing and recognizing client-server commands.
 */
public class CommandExecutor {
    /**
     * At first this method is trying to recognize the command that is contained in Message object, if the command is
     * recognized successful, then it is executed on the server.
     * @param message
     * @param server
     * @throws UnknownCommandException
     */
    protected static void executeCommand(Message message, Server server) throws UnknownCommandException {
        Command concreteCommand;

        if (message.getContent().matches("/kick.+")){
            concreteCommand = new KickCommand(server, message.getContent().substring(6));
        }
        else if (message.getContent().equals("/list")){
            concreteCommand = new ListCommand(server, message.getSender());
        }

        else if (message.getContent().matches("/ban.+")){
            concreteCommand = new BanCommand(server, message.getContent().substring(5));
        }

        else if (message.getContent().equals("/help")){
            concreteCommand = new HelpCommand(server, message);
        }

        else if (message.getContent().equals("/disconnect")){
            concreteCommand = new DisconnectCommand(server, message.getSender());
        }

        else {
            throw new UnknownCommandException("Unknown command.");
        }

        if (concreteCommand != null){
            concreteCommand.execute();  //CONCRETE COMMAND EXECUTION
        }
    }
}
