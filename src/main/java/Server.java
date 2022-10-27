import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Server {
    private DatagramPacket receivePacket;
    private DatagramSocket receiveSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket sendSocket;
    private Game game;

    public Server(Game g) {
        game = g;
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // receive UDP Datagram packets.
            receiveSocket = new DatagramSocket(Config.SERVER_PORT_NUMBER);
            //sending
            sendSocket = new DatagramSocket();
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public String receive() {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram packet is received from receiveSocket.
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("Server Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        int len = receivePacket.getLength();
        // Form a String from the byte array.
        String received = new String(data,0,len);


        return received;
    }
    public void send(String message, int port) {
        byte msg[] = message.getBytes();
        try {
            sendPacket = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Config.LOGGER.info("Server: sending message to " + port);
        System.out.println("Server: sending message to " + port);

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Config.LOGGER.info("Server: Message sent.\n");
        System.out.println("Server: Message sent.\n");

    }

    public void addPlayer() {
        //first, listen for a message
        String message = receive();
        if (message.equals(Config.PLAYER_JOIN_MESSAGE)) {
            //message is good, now we set a player num and return
            int playerNum = game.getPlayerCount() + 1;
            //receive packet still has the last sender's info
            send(Config.SERVER_JOIN_MESSAGE(playerNum), receivePacket.getPort());
            game.addPlayer();
        }
    }

    public void close() {
        receiveSocket.close();
    }
}
