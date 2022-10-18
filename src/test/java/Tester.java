import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

public class Tester {
    @org.junit.jupiter.api.Test
    @DisplayName("PlayerSendTest")
    void PlayerSendTest() {
        //fixture
        Player p = new Player();
        String message = "hello";
        String received = " ";
        try {
            byte data[] = new byte[100];
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);

            // Construct a datagram socket and bind it to port 5000
            // on the local host machine. This socket will be used to
            // receive UDP Datagram packets.
            DatagramSocket receiveSocket = new DatagramSocket(5000);
            try {
                p.rpc_send(message);
                // Block until a datagram packet is received from receiveSocket.
                receiveSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.println("Receive Socket Timed Out.\n" + e);
                e.printStackTrace();
                System.exit(1);
            }
            int len = receivePacket.getLength();
            // Form a String from the byte array.
            received = new String(data,0,len);

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        //assert
        assertEquals(received, message);
    }
    @org.junit.jupiter.api.Test
    @DisplayName("ServerReceiveTest")
    void ServerReceiveTest() {
        //fixture
        Server s = new Server();
        String message = "received";
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets.
            DatagramSocket sendSocket = new DatagramSocket();

            byte msg[] = message.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length,
                        InetAddress.getLocalHost(), 5000);
                try {
                    sendSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        //assert
        assertEquals(message, s.receive());
    }
}
