package com.pawelwuuu;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    //todo dodac rozwiniecia parametrow np --nick oraz opisy
    @Parameter(names = "-n")
    String nick;

    @Parameter(names = "-s")
    boolean isServer = false;

    public static void main(String ... argv) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(argv);
        main.run();
    }

    public void run() {
        if (isServer){
            try{
                String ip = "127.0.0.1";
                Server server = new Server(InetAddress.getByName(ip));
                server.init();
            } catch (UnknownHostException e){
                System.out.println("Something is wrong with IP address or host port.");
            }
        } else {
            Client client = new Client(nick);
            client.userInterface();
        }

    }
}
