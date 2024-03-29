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
    public ArrayList<Integer> playerPorts = new ArrayList<Integer>();
    private int lastPort;
    private String lastMessage;

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
        game.start();
        while(game.getWinner() == 0) {
            //doTurn will tell player 1 to do their turn and wait for a reply.
            System.out.println("================Player" + game.getCurrentTurn() + "s turn " + game.getPlayerCount() + "=============================");
            me.doTurn(game.getCurrentTurn());
            if (game.getCurrentTurn() == 1) {
                //player 1's turn, send updated scoring info:
                me.sendScores();
            }
            if (game.getGameEnder() != 0) {
                //tell player's it is their last Turn.
                me.sendEnding();
            }
        }
        //all done.
        me.EndGame();
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

        Config.LOGGER.info("Server: sending message" + message + " to Player" + (playerPorts.indexOf(port) + 1) + ": at " + port);
        System.out.println("Server: sending message " + message + " to Player" + (playerPorts.indexOf(port) + 1) + ": at " + port);

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Config.LOGGER.info("Server: Message sent.\n");
        System.out.println("Server: Message sent.\n");
        lastMessage = message;
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
            int skullCard = 0;
            if (game.getFortune(player) == 3) {
                skullCard = 1;
            }else if (game.getFortune(player) == 10){
                skullCard = 2;
            }
            //append a "YOU'VE DIED " message if they died
            if (Integer.parseInt(dice.charAt(0)+ "") + skullCard >= 3) {
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

    public void sendScores() {
        //send Scoring to each player sequentially
        String message = "Round: " + game.getRound();
        //make the message
        for (int i = 0; i < game.getPlayerCount(); i++) {
            message = message + " Player" + (i+1) + " Score: " + game.getScores()[i];
        }
        //send em
        for (int i = 0; i < game.getPlayerCount(); i++) {
            send(message, playerPorts.get(i));
        }
    }

    public void sendEnding() {
        //first, send the scores
        sendScores();
        //send final turn message to non game-enders.
        for (int i = 1; i <= game.getPlayerCount(); i++) {
            if (i != game.getGameEnder()) {
                //send finalTurn message
                send("FinalTurn", playerPorts.get(i - 1));
            }
        }
    }

    public void EndGame() {
        //send final scores
        sendScores();
        //send winner and end game messages...
        if (game.getWinner() != 0) {
            for (int i = 1; i <= game.getPlayerCount(); i++) {
                //send winner message
                send("Winner" + game.getWinner(), playerPorts.get(i - 1));
            }
            //game is over...
            close();
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
        //save each player's score
        int[] scores = {0,0,0};
        scores[0] = game.getScores()[0];
        scores[1] = game.getScores()[1];
        scores[2] = game.getScores()[2];
        //got response, score and send back
        send(scorePlayer(response, playerNum), playerPorts.get(playerNum - 1));
        //the player has been scored, let's send deductions, if necessary
        for (int i = 0; i < game.getPlayerCount() - 1; i++) {
            //difference between old and new scores
            int currentDiff = scores[i] - game.getScores()[i];
            if (currentDiff > 0) {
                //send a deduction message to the player.
                send("DEDUCTION " + Config.SERVER_SCORE_MESSAGE(scores[i], game.getScores()[i]), playerPorts.get(i));
            }
        }
    }

    public void close() {
        receiveSocket.close();
        sendSocket.close();
    }

    public Game getGame() {
        return game;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
