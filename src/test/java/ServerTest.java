//import com.pawelwuuu.client.Client;
//import com.pawelwuuu.Message;
//import com.pawelwuuu.server.Server;
//import com.pawelwuuu.jsonUtil.JsonManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.io.DataInputStream;
//import java.net.InetAddress;
//import java.net.Socket;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ServerTest {
//    Server server;
//    Client client1;
//    Client client2;
//    Socket socket1;
//    Socket socket2;
//
//
//    @BeforeEach
//    void initServerClient(){
//        try{
//            server = new Server(InetAddress.getByName("127.0.0.1"));
//        } catch (Throwable e){
//            e.printStackTrace();
//        }
//
//        Thread connectionEstablishThread = new Thread(() -> server.establishConnections());
//        connectionEstablishThread.start();
//
//        client1 = new Client("Adam", "123456", "127.0.0.1");
//        client2 = new Client("Jakub", "123456", "127.0.0.1");
//        connectionEstablishThread.interrupt();
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "Hi,Hello",
//            "What are you doing,Im playing game",
//            "óżź,ćóś",
//            "word word word word word word word word word word word word word word word word , word word word word word word ",
//            "I HoP_e it's working, I hope it;'s working too"
//    })
//    void receiveMessageContentTest(String message1, String message2) {
//        try{
//            client1.sendMessage(client1.createMessage(message1));
//            client2.sendMessage(client2.createMessage(message2));
//
//            Message receivedMessage2 = JsonManager.objectDeserialization(
//                    server.receiveSocketMessage(new DataInputStream(socket1.getInputStream())),
//                    Message.class
//            );
//            Message receivedMessage1 = JsonManager.objectDeserialization(
//                    server.receiveSocketMessage(new DataInputStream(socket2.getInputStream())),
//                    Message.class
//            );
//
//            assertEquals(receivedMessage2, message2);
//            assertEquals(receivedMessage1, message1);
//        } catch (Throwable e){
//            e.printStackTrace();
//        }
//
//    }
//
//
//}