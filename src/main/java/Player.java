import java.io.IOException;
import java.net.*;

public class Player {
    DatagramPacket sendPacket;
    DatagramSocket sendSocket;

    public Player()
    {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets.
            sendSocket = new DatagramSocket();
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }
    public void rpc_send(String message) {
        //construct a packet
        //datagram sends and receives bytes, we will use a more complex system to send and receive event objects.
        byte msg[] = message.getBytes();

        // Construct a datagram packet that is to be sent to a specified port
        // on a specified host.
        // The arguments are:
        //  msg - the message contained in the packet (the byte array)
        //  msg.length - the length of the byte array
        //  InetAddress.getLocalHost() - the Internet address of the
        //     destination host.
        //  5000 - the destination port number on the destination host (this can change)
        try {
            sendPacket = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), Config.SERVER_PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Player: sending message");

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Player: Message sent.\n");
    }
    public void close() {
        sendSocket.close();
    }
}
