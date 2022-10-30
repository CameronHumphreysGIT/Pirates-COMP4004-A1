import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Server {
    private DatagramPacket receivePacket;
    private DatagramSocket receiveSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket sendSocket;
    private Game game;
    private ArrayList<Integer> playerPorts = new ArrayList<Integer>();
    private int lastPort;

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

    public static void main(String[] args) {
        Game game = new Game();
        Server me = new Server(game);
        me.addPlayer();
        //ask the host if they want to close the lobby
        while (me.getGame().getPlayerCount() != Config.MAX_PLAYERS && !me.closeLobby(1)) {
            //host doesn't want to close the lobby, we want to add a player
            me.addPlayer();
        }
        me.sendWelcomes();
        //doTurn will tell player 1 to do their turn and wait for a reply.
        me.doTurn(1);
    }

    public String receive() {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        Config.LOGGER.info("Server: receiving");
        System.out.println("Server: receiving");
        try {
            // Block until a datagram packet is received from receiveSocket.
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("Server Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        int len = receivePacket.getLength();

        //add to playerports if unknown
        if (!playerPorts.contains(receivePacket.getPort())) {
            playerPorts.add(receivePacket.getPort());
        }
        lastPort = receivePacket.getPort();
        // Form a String from the byte array.
        return new String(data,0,len);
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

        Config.LOGGER.info("Server: sending message to Player" + (playerPorts.indexOf(port) + 1) + ": at " + port);
        System.out.println("Server: sending message to Player" + (playerPorts.indexOf(port) + 1) + ": at " + port);

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
            Config.LOGGER.info("Server: Player join request\n");
            System.out.println("Server: Player join request\n");
            //message is good, now we set a player num and return
            int playerNum = game.getPlayerCount() + 1;
            //receive packet still has the last sender's info
            send(Config.SERVER_JOIN_MESSAGE(playerNum), playerPorts.get(playerNum - 1));
            game.addPlayer();
        }
    }

    public boolean closeLobby(int n) {
        //ask player if they want to close the lobby
        send("Would you like to close the Lobby?", playerPorts.get(n - 1));
        String response = receive();
        //loop until Y or N
        while (!(response.equals("Y") || response.equals("N"))) {
            Config.LOGGER.info("Server: bad response, likely the wrong player");
            System.out.println("Server: bad response, likely the wrong player");
            response = receive();
        }
        if (response.equals("Y")) {
            Config.LOGGER.info("Server: Player responded Y");
            System.out.println("Server: Player responded Y");
            game.start();
            return true;
        }else {
            Config.LOGGER.info("Server: Player responded N");
            System.out.println("Server: Player responded N");
            return false;
        }
    }

    public String scorePlayer(String dice, int player) {
        //get the initial score
        int first = game.getScores()[player - 1];
        if (game.score(dice, player)) {
            //final score is calculated and set already
            String death = "";
            //append a "YOU'VE DIED " message if they died
            if (Integer.parseInt(dice.charAt(0)+ "") >= 3) {
                death = "YOU'VE DIED ";
            }
            return death + Config.SERVER_SCORE_MESSAGE(first, game.getScores()[player - 1]);
        }else {
            return "ERROR, Invalid dice combination";
        }
    }

    public void sendWelcomes() {
        //send welcome to each player sequentially
        for (int i = 0; i < game.getPlayerCount(); i++) {
            send("Welcome", playerPorts.get(i));
        }
    }

    public void doTurn(int playerNum) {
        send("It's you're turn" + game.getFortune(playerNum), playerPorts.get(playerNum - 1));
        //receive the player's response(a set of die to score)
        String response = receive();
        //loop until response is from the expected player
        while (lastPort != playerPorts.get(playerNum - 1)) {
            Config.LOGGER.info("Server: bad response, likely the wrong player");
            response = receive();
        }
        //got response, score and send back
        send(scorePlayer(response, playerNum), playerPorts.get(playerNum - 1));
    }

    public void close() {
        receiveSocket.close();
        sendSocket.close();
    }

    public Game getGame() {
        return game;
    }
}
