import java.io.IOException;
import java.net.*;

public class Player {
    private DatagramPacket sendPacket;
    //server will send to the address data it received from
    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;
    private DatagramPacket receivePacket;
    private String lastMessage;
    private int number;

    public Player()
    {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets, add timeout for receiving
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(Config.PLAYER_PORT_NUMBER);
            receiveSocket.setSoTimeout(Config.TIMEOUT);
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
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Player: Message sent.\n");

        //wait for a reply with a timeout
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        System.out.println("Player: receiving message");

        try {
            // Block until a datagram packet is received from receiveSocket.
            receiveSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            //set last message to timeout
            lastMessage = "Timeout";
            return;
        } catch (IOException e) {
            System.out.println("Player IO Exception.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Player: message received");

        int len = receivePacket.getLength();
        // Form a String from the byte array, set to lastMessage
        lastMessage = new String(data,0,len);
    }
    public boolean join() {
        //send join request and receive
        rpc_send("Join Request");

        //parse response
        if (lastMessage.equals("Timeout")) {
            //server busy or whatever
            number = 0;
            return false;
        }
        int lastdigit = Integer.parseInt(String.valueOf(lastMessage.charAt(lastMessage.length() - 1)));
        //check that it's a return message
        if (lastMessage.equals(Config.JOIN_MESSAGE(lastdigit))) {
            //Set the number to be the last digit of the message
            number = lastdigit;
            return true;
        }

        return false;
    }

    public void close() {
        sendSocket.close();
        receiveSocket.close();
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public int getNumber() {
        return number;
    }
}
