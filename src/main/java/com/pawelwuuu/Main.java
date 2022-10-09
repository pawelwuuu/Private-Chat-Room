//package com.pawelwuuu;
//
//import com.beust.jcommander.JCommander;
//import com.beust.jcommander.Parameter;
//import com.pawelwuuu.client.Client;
//import com.pawelwuuu.server.Server;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//public class Main {
//    @Parameter(names = {"-n", "--nick"}, description = "User nickname.")
//    String nick;
//
//    @Parameter(names = {"-s", "--server"}, description = "Defines if client is also a server.")
//    boolean isServer = false;
//
//    @Parameter(names = {"-p", "--password"}, description = "Password to chat room.")
//    String password = "DEFAULT";
//
//    public static void main(String ... argv) {
//        Main main = new Main();
//        JCommander.newBuilder()
//                .addObject(main)
//                .build()
//                .parse(argv);
//
//        main.run();
//    }
////todo add command pattern
//    public void run() {
//        InetAddress ip = null;
//        try{
//            ip = InetAddress.getLocalHost();
//        } catch (UnknownHostException e){
//            System.out.println("Something is wrong with IP address or host port.");
//            return;
//        }
//
//        try {
//
//            if (isServer){
//                Server server = new Server(ip, password);
//                server.init(false);
//
//                Client client = new Client(nick, password, ip.getHostAddress());
//                client.userInterface();
//
//            } else{
//                Client client = new Client(nick, password, ip.getHostAddress());
//                client.userInterface();
//            }
//        } catch (Throwable e){
//            System.out.println(e.getMessage());
//        }
//    }
//}
