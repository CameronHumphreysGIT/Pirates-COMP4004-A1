import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Player {
    private DatagramPacket sendPacket;
    //server will send to the address data it received from
    private DatagramSocket sendSocket;
    private DatagramPacket receivePacket;
    private String lastMessage;
    private int number = 0;

    public Player() {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets, add timeout for receiving
            sendSocket = new DatagramSocket();
            sendSocket.setSoTimeout(Config.TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }
    //constructor for testing
    public Player(int port) {
        try {
            sendSocket = new DatagramSocket(port);
            sendSocket.setSoTimeout(Config.TIMEOUT);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Player me = new Player();
        boolean joined = me.join();
        while (!joined) {
          joined = me.join();
        }
        //good connection, receive
        me.receive();
        //lobbyWait will wait until we get a Welcome message, and collect input from player 1 if necessary.
        me.lobbyWait();
        //do a welcome message
        Config.LOGGER.info(Config.WELCOME);
        System.out.println(Config.WELCOME);

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

        //Config.LOGGER.info("Player" + number + ": sending message");
        System.out.println("Player" + number + ": sending message");

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Config.LOGGER.info("Player" + number + ": message sent\n");
        System.out.println("Player" + number + ": message sent\n");
        receive();
    }

    public void receive() {
        //wait for a reply with a timeout
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        //Config.LOGGER.info("Player" + number + ": receiving message");
        System.out.println("Player" + number + ": receiving message");

        try {
            // Block until a datagram packet is received from receiveSocket.
            sendSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            //set last message to timeout
            lastMessage = "Timeout";
            return;
        } catch (IOException e) {
            System.out.println("Player IO Exception.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        //Config.LOGGER.info("Player" + number + ": message received");
        System.out.println("Player" + number + ": message received");

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
            //Config.LOGGER.info("Player" + number + ": failed to join Server");
            System.out.println("Player" + number + ": failed to join Server");
            number = 0;
            return false;
        }
        int lastdigit = Integer.parseInt(String.valueOf(lastMessage.charAt(lastMessage.length() - 1)));
        //check that it's a return message
        if (lastMessage.equals(Config.SERVER_JOIN_MESSAGE(lastdigit))) {
            //Set the number to be the last digit of the message
            number = lastdigit;
            Config.LOGGER.info("Player" + number + ": successfully joined the Server");
            System.out.println("Player" + number + ": successfully joined the Server");
            return true;
        }

        return false;
    }

    public void lobbyWait() {
        //if we are player 1, receive until we get the lobby question
        if (number == 1) {
            String msg;
            Scanner input = new Scanner(System.in);
            while(!(lastMessage.equals("Welcome"))) {
                while (!lastMessage.equals("Would you like to close the Lobby?")) {
                    System.out.println(lastMessage);
                    receive();
                }
                Config.LOGGER.info(getLastMessage() + " {Y/N}");
                System.out.println(getLastMessage() + " {Y/N}");
                msg = input.nextLine();
                while (!(msg.equals("Y") || msg.equals("N"))) {
                    Config.LOGGER.info("Invalid input, try again");
                    System.out.println("Invalid input, try again");
                    msg = input.nextLine();
                }
                rpc_send(msg);
            }
        }else {
            //not lobby host, just wait for welcome card
            while (!lastMessage.equals("Welcome")) {
                System.out.println(lastMessage);
                receive();
            }
        }
    }

    public void close() {
        sendSocket.close();
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public int getNumber() {
        return number;
    }
}
