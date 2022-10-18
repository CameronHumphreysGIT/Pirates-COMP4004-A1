import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Server {
    DatagramPacket receivePacket;
    DatagramSocket receiveSocket;

    public Server()
    {
        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // receive UDP Datagram packets.
            receiveSocket = new DatagramSocket(5000);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    public String receive() {
        byte data[] = new byte[100];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram packet is received from receiveSocket.
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        int len = receivePacket.getLength();
        // Form a String from the byte array.
        String received = new String(data,0,len);


        return received;
    }
}
