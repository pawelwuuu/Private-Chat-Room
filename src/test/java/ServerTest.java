import com.pawelwuuu.Client;
import com.pawelwuuu.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    Server server;
    Client client1;
    Client client2;
    Socket socket1;
    Socket socket2;


//    @BeforeEach
//    void initServerClient(){
//        try{
//            server = new Server(InetAddress.getByName("127.0.0.1"));
//        } catch (Throwable e){
//            e.printStackTrace();
//        }
//
//        Thread connectionEstablisher = new Thread(() -> server.establishConnections());
//        connectionEstablisher.start();
//
//        client1 = new Client("Adam");
//        client2 = new Client("Jakub");
//        connectionEstablisher.interrupt();
//    }

//    @Test
//    void receiveMessageTest() {
//        client1.sendMessage(client1.createMessage("Hi!"));
//        client2.sendMessage(client2.createMessage("Hello!"));
//
//        try{
//            String message1 = server.receiveSocketMessage(new DataInputStream(socket1.getInputStream()));
//            String message2 = server.receiveSocketMessage(new DataInputStream(socket2.getInputStream()));
//
//            assertEquals(message1, "{\"content\":\"Hi!\",\"sender\":\"Adam\"}");
//            assertEquals(message2, "{\"content\":\"Hello!\",\"sender\":\"Jakub\"}");
//        } catch (Throwable e){
//            e.printStackTrace();
//        }
//
//    }


}