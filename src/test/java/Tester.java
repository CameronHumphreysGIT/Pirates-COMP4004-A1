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
        DatagramSocket receiveSocket = setupSocket(true);

        byte data[] = new byte[100];
        DatagramPacket receivePacket = setupPacket(data, true);
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
        //assert
        assertEquals(received, message);
    }
    @org.junit.jupiter.api.Test
    @DisplayName("ServerReceiveTest")
    void ServerReceiveTest() {
        //fixture
        Server s = new Server();
        String message = "received";
        DatagramSocket sendSocket = setupSocket(false);
        byte msg[] = message.getBytes();
        DatagramPacket sendPacket = setupPacket(msg, false);
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //assert
        assertEquals(message, s.receive());
    }
    DatagramSocket setupSocket(boolean receive) {
        DatagramSocket testSocket;
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            //send or receive
            if (receive) {
                testSocket = new DatagramSocket(5000);
            }else {
                testSocket = new DatagramSocket();
            }
            return testSocket;
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    DatagramPacket setupPacket(byte[] data, boolean receive) {
        DatagramPacket testPacket = null;
        if (receive) {
            testPacket = new DatagramPacket(data, data.length);
        }else {
            try {
                testPacket = new DatagramPacket(data, data.length,
                        InetAddress.getLocalHost(), 5000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return testPacket;
    }
}
