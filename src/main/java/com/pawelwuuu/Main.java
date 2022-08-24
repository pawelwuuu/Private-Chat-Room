package com.pawelwuuu;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    @Parameter(names = {"-n", "--nick"}, description = "User nickname.")
    String nick;

    @Parameter(names = {"-s", "--server"}, description = "Defines if client is also a server.")
    boolean isServer = false;

    @Parameter(names = {"-p", "--password"}, description = "Password to chat room.")
    String password;

    public static void main(String ... argv) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(argv);
        main.run();
    }
//todo add command pattern
    public void run() {
        if (isServer){
            try{
                String ip = "127.0.0.1";
                Server server = new Server(InetAddress.getByName(ip), password);
                server.init();
            } catch (UnknownHostException e){
                System.out.println("Something is wrong with IP address or host port.");
            }
        } else{
            Client client = new Client(nick, password, "127.0.0.1");
            client.userInterface();
        }

    }
}
