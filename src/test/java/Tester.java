import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

public class Tester {
    @org.junit.jupiter.api.Test
    @DisplayName("PlayerSendTest")
    void PlayerSendTest() {
        //fixture
        Player p = new Player();
        String message = "hello";
        String received = " ";
        DatagramSocket receiveSocket = setupSocket(true, true);

        byte data[] = new byte[100];
        DatagramPacket receivePacket = setupPacket(data, true, false);
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
        DatagramSocket sendSocket = setupSocket(false, true);
        byte msg[] = message.getBytes();
        DatagramPacket sendPacket = setupPacket(msg, false, true);
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
        DatagramSocket sendSocket = setupSocket(false, false);
        byte[] data = message.getBytes();
        DatagramPacket sendPacket = setupPacket(data, false, false);
        //get the player to send something, and confirm it
        if (message.equals("message")) {
            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //call rpc_send, don't worry if it sends or not.
        p.rpc_send("hello");

        //assert, where player stores the reply
        assertEquals(message, p.getLastMessage());

        //teardown
        p.close();
        datagramTeardown(sendSocket, sendPacket);
    }
    @org.junit.jupiter.api.Test
    @DisplayName("ServerSendTest")
    void ServerSendTest() {
        //fixture
        Server s = new Server();
        String message = "hello";
        String received = " ";
        DatagramSocket receiveSocket = setupSocket(true, false);

        byte data[] = new byte[100];
        DatagramPacket receivePacket = setupPacket(data, true, false);
        try {
            //needs to know the port number, since, unlike the player, may send to other entities
            s.send(message, Config.PLAYER_PORT_NUMBER);
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
        s.close();
        datagramTeardown(receiveSocket, receivePacket);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 0})
    @DisplayName("PlayerJoinTest")
    void PlayerJoinTest(int playerNum) {
        //make player
        Player p = new Player();

        //in the 0 case we won't send a server response
        if (playerNum != 0) {
            //send "You are Playerx"
            DatagramSocket sendSocket = setupSocket(false, false);
            byte[] data = Config.JOIN_MESSAGE(playerNum).getBytes();
            DatagramPacket sendPacket = setupPacket(data, false, false);
            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            //use player.join which returns a boolean...
            assertTrue(p.join());
            //teardown
            datagramTeardown(sendSocket, sendPacket);
        }else {
            assertFalse(p.join());
        }

        //check that player.number is x
        assertEquals(playerNum, p.getNumber());

        //teardown
        p.close();
    }



    //Helpers

    DatagramSocket setupSocket(boolean receive, boolean isServer) {
        DatagramSocket testSocket = null;
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            //send or receive
            if (receive) {
                if (isServer) {
                    testSocket = new DatagramSocket(Config.SERVER_PORT_NUMBER);
                }else {
                    testSocket = new DatagramSocket(Config.PLAYER_PORT_NUMBER);
                }
            }else {
                testSocket = new DatagramSocket();
            }
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
        return testSocket;
    }

    DatagramPacket setupPacket(byte[] data, boolean receive, boolean isServer) {
        DatagramPacket testPacket = null;
        if (receive) {
            testPacket = new DatagramPacket(data, data.length);
        }else {
            try {
                if (isServer) {
                    testPacket = new DatagramPacket(data, data.length,
                            InetAddress.getLocalHost(), Config.SERVER_PORT_NUMBER);
                }else {
                    testPacket = new DatagramPacket(data, data.length,
                            InetAddress.getLocalHost(), Config.PLAYER_PORT_NUMBER);
                }
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
