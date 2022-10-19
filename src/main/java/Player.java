import java.io.IOException;
import java.net.*;

public class Player {
    private DatagramPacket sendPacket;
    //server will send to the address data it received from
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket receivePacket;
    private String lastMessage;

    public Player()
    {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets, add timeout for receiving
            sendReceiveSocket = new DatagramSocket();
            sendReceiveSocket.setSoTimeout(Config.TIMEOUT);
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

        try {
            sendPacket = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), Config.SERVER_PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Player: sending message");

        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Player: Message sent.\n");

        //wait for a reply with a timeout
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram packet is received from sendReceiveSocket.
            sendReceiveSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            //set last message to timeout
            lastMessage = "Timeout";
        } catch (IOException e) {
            System.out.println("Player IO Exception.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        int len = receivePacket.getLength();
        // Form a String from the byte array, set to lastMessage
        lastMessage = new String(data,0,len);
    }
    public void close() {
        sendReceiveSocket.close();
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
