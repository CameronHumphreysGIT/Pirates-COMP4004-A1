import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class Tester {
    @Nested
    @DisplayName("NetworkTests")
    class NetworkTests {
        @Nested
        @DisplayName("PlayerTests")
        class PlayerTests {
            @Test
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
                received = new String(data, 0, len);
                //assert
                assertEquals(received, message);
                //teardown
                p.close();
                datagramTeardown(receiveSocket, receivePacket);
            }
            @ParameterizedTest
            @ValueSource(strings = {"message", "Timeout"})
            @DisplayName("PlayerRPCTest")
            void PlayerRPCTest(String message) {
                //fixture
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
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
            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 0})
            @DisplayName("PlayerJoinTest")
            void PlayerJoinTest(int playerNum) {
                //make player
                Player p = new Player(Config.PLAYER_PORT_NUMBER);

                //in the 0 case we won't send a server response
                if (playerNum != 0) {
                    //send "You are Playerx"
                    DatagramSocket sendSocket = setupSocket(false, false);
                    byte[] data = Config.SERVER_JOIN_MESSAGE(playerNum).getBytes();
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
                } else {
                    assertFalse(p.join());
                }

                //check that player.number is x
                assertEquals(playerNum, p.getNumber());

                //teardown
                p.close();
            }
        }
        @Nested
        @DisplayName("ServerTests")
        class ServerTests {
            @Test
            @DisplayName("ServerReceiveTest")
            void ServerReceiveTest() {
                //fixture
                Game g = new Game();
                Server s = new Server(g);
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

            @Test
            @DisplayName("ServerSendTest")
            void ServerSendTest() {
                //fixture
                Game g = new Game();
                Server s = new Server(g);
                String message = "hello";
                String received = " ";
                DatagramSocket receiveSocket = setupSocket(true, false);

                byte data[] = new byte[100];
                DatagramPacket receivePacket = setupPacket(data, true, false);
                try {
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
                received = new String(data, 0, len);
                //assert
                assertEquals(received, message);
                //teardown
                s.close();
                datagramTeardown(receiveSocket, receivePacket);
            }
            @Test
            @DisplayName("ServerAddPlayerTest")
            void ServerAddPlayerTest() {
                //make Game object
                Game g = new Game();
                //make server with game object param
                Server s = new Server(g);
                //send moque player join message
                DatagramSocket sendSocket = setupSocket(false, true);
                byte msg[] = Config.PLAYER_JOIN_MESSAGE.getBytes();
                DatagramPacket sendPacket = setupPacket(msg, false, true);
                try {
                    sendSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                //run server.addPlayer()
                s.addPlayer();
                //check game.playercount
                assertEquals(1, g.getPlayerCount());
                //teardown
                s.close();
                datagramTeardown(sendSocket, sendPacket);
            }
        }
        @Nested
        @DisplayName("Log4JTests")
        class Log4JTests {
            @Test
            @DisplayName("Log4J3PlayersJoinServerTest")
            void AllPlayersJoinTest() {
                //run Server
                //start Player1
                //ensure Server asks player1 if lobby should close (N as response)
                //start Player2
                //ensure Server asks player1 if lobby should close (N as response)
                //start Player3
                //check that all players have been added properly, and have corresponding player numbers.

                //expected logs in the Log4J3PlayersJoinServerTest.log file
            }
        }
        @Nested
        @DisplayName("Log4JTestsOld")
        class Log4JTestsOld {
            //Legacy Unit testing, all of which are covered by Log4JTests
            @Test
            @DisplayName("Log4JPlayerJoinServerTest")
            void PlayerJoinServerTest() {
                //server has a main method that calls addPlayer on init.
                //Player has a main method that calls join on init
                //if server is run before player, the logs should show successful joining of the server and a player declaration

                //expected output
                /*

                [INFO ] 2022-10-27 15:28:28.358 [main] Config - Player: sending message
                [INFO ] 2022-10-27 15:28:28.360 [main] Config - Player: Message sent.

                [INFO ] 2022-10-27 15:28:28.361 [main] Config - Player: receiving message
                [INFO ] 2022-10-27 15:28:28.811 [main] Config - Server: Player join request

                [INFO ] 2022-10-27 15:28:28.815 [main] Config - Server: sending message to 3010
                [INFO ] 2022-10-27 15:28:28.815 [main] Config - Server: Message sent.

                [INFO ] 2022-10-27 15:28:28.816 [main] Config - Player: message received
                [INFO ] 2022-10-27 15:28:28.816 [main] Config - Player1: successfully joined the Server
                 */
            }
            @Test
            @DisplayName("Log4JPlayerFailsToJoinTest")
            void PlayerFailsToJoinTest() {
                //player init
                //player tries connection
                //fails
                //tries until succeeds

                //expected output
                /*

                [INFO ] 2022-10-27 15:55:18.572 [main] Config - Player: sending message
                [INFO ] 2022-10-27 15:55:18.574 [main] Config - Player: Message sent.

                [INFO ] 2022-10-27 15:55:18.574 [main] Config - Player: receiving message
                [INFO ] 2022-10-27 15:55:23.578 [main] Config - Player: failed to join Server
                [INFO ] 2022-10-27 15:55:23.579 [main] Config - Player: sending message
                [INFO ] 2022-10-27 15:55:23.580 [main] Config - Player: Message sent.

                [INFO ] 2022-10-27 15:55:23.580 [main] Config - Player: receiving message
                [INFO ] 2022-10-27 15:55:28.589 [main] Config - Player: failed to join Server
                [INFO ] 2022-10-27 15:55:28.591 [main] Config - Player: sending message
                [INFO ] 2022-10-27 15:55:28.592 [main] Config - Player: Message sent.

                [INFO ] 2022-10-27 15:55:28.592 [main] Config - Player: receiving message
                [INFO ] 2022-10-27 15:55:33.607 [main] Config - Player: failed to join Server
                [INFO ] 2022-10-27 15:55:33.609 [main] Config - Player: sending message
                [INFO ] 2022-10-27 15:55:33.610 [main] Config - Player: Message sent.

                [INFO ] 2022-10-27 15:55:33.610 [main] Config - Player: receiving message
                 */
            }
            @Test
            @DisplayName("Log4JPlayerTakesSimpleTurnTest")
            void Log4JPlayerTakesSimpleTurnTest() {

            }
        }
    }
    @Nested
    @DisplayName("GameTests")
    class GameTests {
        @Test
        @DisplayName("PlayerDiceRoll")
        void PlayerDiceRoll() {
            //create a player
            Player p = new Player();
            //use dice roll function
            p.rollDice();
            //confirm each dice is of the possible dice and add them up
            int[] countDie = new int[6];
            ArrayList<String> playerDice = p.getDice();
            for (String d : playerDice) {
                //indexOf returns -1 as an error value
                assertNotEquals(Config.DICE.indexOf(d), -1);
                countDie[Config.DICE.indexOf(d)]++;
            }
            String diceString = p.getDiceString();
            //we want the diceString to be a sum of each type of dice in the config specified order.
            assertEquals("" + countDie[0] + countDie[1] + countDie[2] + countDie[3] + countDie[4] + countDie[5], diceString);

        }
        @ParameterizedTest
        @ValueSource(strings = {"020000", "123456", "0131210", "150210","-103402"})
        @DisplayName("ScoreDiceInvalidTest")
        void ScoreDiceInvalidTest(String dice) {
            //create a game
            Game g = new Game();
            g.start();
            //call score function with a given player.
            assertFalse(g.score(dice, 1));
            //check the player's score has not changed
            assertEquals(0, g.getScores()[0]);
            //check that the current Turn hasn't changed
            assertEquals(1, g.getCurrentTurn());
        }
        @Test
        @DisplayName("45PlayerOneTurnTest")
        void PlayerOneTurnTest() {
            Game g = new Game();
            Server s = new Server(g);
            Player p = new Player(Config.PLAYER_PORT_NUMBER);
            p.setTurn(true);
            //setup is just an example
            ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "MONKEY", "PARROT", "GOLD", "SKULL", "PARROT"));
            p.rollDice();
            p.setDice(setup);
            //let's simulate the server to send the response
            DatagramSocket sendSocket = setupSocket(false, true);
            //scorePlayer will send a nice response for the player
            byte msg[] = s.scorePlayer(p.getDiceString(), 1).getBytes();
            DatagramPacket sendPacket = setupPacket(msg, false, false);
            try {
                sendSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            p.endTurn();
            //Server Score message is the word response the server gives with a given initial and final score, which should be zero.
            assertEquals(Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
            //shouldn't be the player's turn anymore
            assertFalse(p.getTurn());
            //ensure Server asks player1 if lobby should close (Y as response)
            //Player is told it's their turn
            //Player rolls dice and sees a list describing their roll
            //Player sends the dice to the server for scoring and receives a score
        }
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
