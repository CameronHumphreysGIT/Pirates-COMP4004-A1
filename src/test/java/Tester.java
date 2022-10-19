import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        //teardown
        p.close();
        datagramTeardown(receiveSocket, receivePacket);
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
        //teardown
        s.close();
        datagramTeardown(sendSocket, sendPacket);
    }

    @ParameterizedTest
    @ValueSource(strings = {"message", "Timeout"})
    @DisplayName("PlayerRPCTest")
    void PlayerRPCTest(String message) {
        //fixture
        Player p = new Player();
        if (message == "message") {
            DatagramSocket sendSocket = setupSocket(false);
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = setupPacket(data, false);
            //get the player to send something, and confirm it
            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        PlayerSendTest();

        //assert, where player stores the reply
        assertEquals(p.getLastMessage(), message);
    }

    DatagramSocket setupSocket(boolean receive) {
        DatagramSocket testSocket = null;
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            //send or receive
            if (receive) {
                testSocket = new DatagramSocket(Config.SERVER_PORT_NUMBER);
            }else {
                testSocket = new DatagramSocket();
            }
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        return testSocket;
    }

    DatagramPacket setupPacket(byte[] data, boolean receive) {
        DatagramPacket testPacket = null;
        if (receive) {
            testPacket = new DatagramPacket(data, data.length);
        }else {
            try {
                testPacket = new DatagramPacket(data, data.length,
                        InetAddress.getLocalHost(), Config.SERVER_PORT_NUMBER);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return testPacket;
    }

    void datagramTeardown (DatagramSocket s, DatagramPacket p) {
        s.close();
        //let the garbage collector do it's thing
        s = null;
        p = null;
    }
}
