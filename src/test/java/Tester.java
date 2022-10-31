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
                System.out.println(p.getLastMessage());

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
                //server init
                //player init
                //ensure Server asks player1 if lobby should close (Y as response)
                //Player is told it's their turn
                //Player rolls dice and sees a list describing their roll
                //Player sends the dice to the server for scoring and receives a score
                //Player is returned to waiting for their turn...

                //expected logs in the Log4JPlayerTakesSimpleTurnTest.log file
            }
            @Test
            @DisplayName("Log4JPlayerTakesReRollTurnTest")
            void Log4JPlayerTakesReRollTurnTest() {
                //server init
                //player init
                //ensure Server asks player1 if lobby should close (Y as response)
                //Player is told it's their turn
                //Player rolls dice and sees a list describing their roll
                //Player is asked if they would like to reroll or end their turn
                //Player may reRoll until their dice have three skulls, or they with to end their turn
                //Player sends the dice to the server for scoring and receives a score
                //Player is returned to waiting for their turn...

                //expected logs in the Log4JPlayerTakesReRollTurnTest.log file
            }
            @Test
            @DisplayName("Log4JPlayerIsSorceressTurnTest")
            void Log4JPlayerIsSorceressTurnTest() {
                //uncomment line(24) in game to ensure sorceress as player's card.
                //server init
                //player init
                //ensure Server asks player1 if lobby should close (Y as response)
                //Player is told it's their turn
                //player get's three skulls and doesn't immediately die
                //player tries rerolling 2 skulls (can't)
                //player tries rerolling 1 skull twice (can't)
                //player submits for scoring.

                //comment 24 in game afterwards
            }
            @Test
            @DisplayName("Log4JPlayerTreasureTest")
            void Log4JPlayerTreasureTest() {
                //uncomment line(25) in game to ensure Treasure as player's card.
                //server init
                //player init
                //ensure Server asks player1 if lobby should close (Y as response)
                //Player is told it's their turn
                //player put's things in their chest
                //player removes things from their chest
                //player can put 1 (non skull) in their chest
                //player submits for scoring/dies

                //comment 25 in game afterwards
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
            p.setFortune(Config.FORTUNE_CARDS.get(7));
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
            if (countDie[0] >= 4) {
                //skull island
                assertEquals("" + countDie[0] + countDie[1] + countDie[2] + countDie[3] + countDie[4] + countDie[5] + 0, diceString);
            }else {
                //we want the diceString to be a sum of each type of dice in the config specified order.
                assertEquals("" + countDie[0] + countDie[1] + countDie[2] + countDie[3] + countDie[4] + countDie[5], diceString);
            }


        }
        @ParameterizedTest
        @ValueSource(strings = {"020000", "123456", "0131212", "150210","-103402"})
        @DisplayName("ScoreDiceInvalidTest")
        void ScoreDiceInvalidTest(String dice) {
            //create a game
            Game g = new Game();
            g.start();
            //treasurechest can mess with testing
            g.setFortune("GOLD", 1);
            //call score function with a given player.
            assertFalse(g.score(dice, 1));
            //check the player's score has not changed
            assertEquals(0, g.getScores()[0]);
            //check that the current Turn hasn't changed
            assertEquals(1, g.getCurrentTurn());
        }
        @ParameterizedTest
        @ValueSource(strings = {"12345672", "8", "-1", "1", "01g"})
        @DisplayName("PlayerReRollInvalidTest")
        void PlayerReRollInvalidTest(String reRoll) {
            //cannot reroll more then there are dice
            //cannot reroll a non-index
            //cannot reroll a skull
            //cannot reroll a letter
            Player p = new Player();
            p.setFortune(Config.FORTUNE_CARDS.get(7));
            ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SKULL", "PARROT", "MONKEY", "PARROT", "GOLD", "DIAMOND", "PARROT"));
            p.setDice(setup);
            //reroll
            assertFalse(p.reRoll(reRoll));
            //make sure we have the same dice as we started
            assertEquals(setup, p.getDice());
        }

        @ParameterizedTest
        @ValueSource(strings = {"023567", "62", "30"})
        @DisplayName("PlayerReRollValidTest")
        void PlayerReRollValidTest(String reRoll) {
            //avoids rerolling a skull
            //repeated nums are ignored
            Player p = new Player();
            p.setFortune(Config.FORTUNE_CARDS.get(7));
            ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SKULL", "PARROT", "MONKEY", "SKULL", "GOLD", "DIAMOND", "PARROT"));
            p.setDice(setup);
            //reroll
            assertTrue(p.reRoll(reRoll));
            char[] chars = reRoll.toCharArray();
            //make sure the dice we did reRoll are different
            for (int j = 0; j < chars.length; j++) {
                //basically, reroll ensures that every rerolled dice changes.
                int current = Integer.parseInt("" + chars[j]);
                assertNotEquals(p.getDice().get(current), setup.get(current));
            }
        }
        @Nested
        @DisplayName("AcceptancePart1Tests")
        class AcceptancePartOneTests {
            @Test
            @DisplayName("45PlayerOneTurnTest")
            void FortyFiveTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup is just an example, with 3 skulls
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "SWORD", "SWORD", "SWORD", "SKULL", "SWORD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero.
                assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
            @Test
            @DisplayName("46PlayerOneTurnRerollTest")
            void FortySixTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 46
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SWORD", "PARROT", "SWORD", "SWORD", "PARROT"));
                //does the setup and rerolls once for us
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SKULL", "PARROT", "SKULL", "SWORD", "PARROT")), "356", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("47PlayerOneTurnRerollTest")
            void FortySevenTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 47
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SWORD", "PARROT", "SWORD", "SKULL", "PARROT"));
                //does the setup and rerolls once for us
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SKULL", "PARROT", "SWORD", "SKULL", "PARROT")), "35", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("48PlayerOneTurnRerollTest")
            void FortyEightTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 48
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SWORD", "PARROT", "SWORD", "SWORD", "PARROT"));
                //does the setup and rerolls twice for us
                twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SKULL", "PARROT", "MONKEY", "MONKEY", "PARROT")), new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SKULL", "PARROT", "SKULL", "MONKEY", "PARROT")), "356","56", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("50PlayerOneTurnRerollTest")
            void FiftyTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 50
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SKULL", "PARROT", "SWORD", "GOLD", "SWORD", "SWORD", "GOLD"));
                //does the setup and rerolls once for us
                twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("GOLD", "SKULL", "GOLD", "SWORD", "GOLD", "SWORD", "SWORD", "GOLD")), new ArrayList<>(Arrays.asList("GOLD", "SKULL", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD")), "02", "356", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 4800), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("52PlayerOneTurnRerollTest")
            void FiftyTwoTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 52
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("DIAMOND", "PARROT", "PARROT", "MONKEY", "DIAMOND", "GOLD", "GOLD", "MONKEY"));
                //this helper runs everything for us
                noReRollTest(p, setup, 1);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 800), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("53PlayerOneTurnRerollTest")
            void FiftyThreeTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 53
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SKULL", "MONKEY", "PARROT", "SWORD", "PARROT", "SWORD", "SKULL"));
                //does the setup and rerolls for us
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "SKULL", "MONKEY", "MONKEY", "SWORD", "SWORD", "SWORD", "SKULL")), "35", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 300), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("54PlayerOneTurnRerollTest")
            void FiftyFourTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 54
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SWORD", "SWORD", "MONKEY", "SWORD", "SKULL", "MONKEY", "SKULL", "MONKEY"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 300), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("55PlayerOneTurnRerollTest")
            void FiftyFiveTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 55
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("DIAMOND", "DIAMOND", "SWORD", "DIAMOND", "PARROT", "SKULL", "SKULL", "MONKEY"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("56PlayerOneTurnRerollTest")
            void FiftySixTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 56
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("GOLD", "GOLD", "SKULL", "SWORD", "GOLD", "SWORD", "SKULL", "GOLD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 8);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 700), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("57PlayerOneTurnRerollTest")
            void FiftySevenTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 57
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "PARROT", "PARROT", "SWORD", "PARROT", "SWORD", "PARROT", "SWORD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 400), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("58PlayerOneTurnRerollTest")
            void FiftyEightTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 58
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SWORD", "SWORD", "PARROT", "PARROT", "SWORD", "GOLD", "SKULL", "GOLD"));
                //does the setup and rerolls for us
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("SWORD", "SWORD", "GOLD", "SWORD", "SWORD", "GOLD", "SKULL", "GOLD")), "23", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 800), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("59PlayerOneTurnRerollTest")
            void FiftyNineTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 59
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SWORD", "SWORD", "PARROT", "PARROT", "SWORD", "GOLD", "SKULL", "GOLD"));
                //does the setup and rerolls for us
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("SWORD", "SWORD", "GOLD", "SWORD", "SWORD", "GOLD", "SKULL", "GOLD")), "23", 1);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1200), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("60PlayerOneTurnRerollTest")
            void SixtyTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 60
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "PARROT", "SWORD", "MONKEY", "SKULL", "SWORD"));
                //this will do the second reroll
                twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("SKULL", "SWORD", "PARROT", "PARROT", "SWORD", "SWORD", "SKULL", "SWORD")), new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "MONKEY", "SWORD", "SWORD", "SKULL", "SWORD")), "05", "23", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 600), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("62PlayerOneTurnRerollTest")
            void SixtyTwoTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 62
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "MONKEY", "SKULL", "MONKEY", "MONKEY", "MONKEY", "SKULL", "MONKEY"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1100), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("63PlayerOneTurnRerollTest")
            void SixtyThreeTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 63
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "SKULL", "PARROT"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 2100), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("64PlayerOneTurnRerollTest")
            void SixtyFourTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 64
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 5400), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("65PlayerOneTurnRerollTest")
            void SixtyFiveTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 65
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD", "GOLD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 8);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 5400), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("66PlayerOneTurnRerollTest")
            void SixtySixTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 66
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD"));
                //this helper runs everything for us
                noReRollTest(p, setup, 1);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 9000), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("67PlayerOneTurnRerollTest")
            void SixtySevenTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 67
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "MONKEY", "SWORD", "MONKEY", "MONKEY", "MONKEY"));
                //this does the test for us...
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "MONKEY", "MONKEY", "MONKEY", "MONKEY", "MONKEY", "MONKEY", "MONKEY")), "14", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 4600), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("68PlayerOneTurnRerollTest")
            void SixtyEightTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 68
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "PARROT", "SWORD", "MONKEY", "SKULL", "SKULL"));
                //this will do the second reroll
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "DIAMOND", "DIAMOND", "SWORD", "MONKEY", "SKULL", "SKULL")), "23", 8);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 400), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("69PlayerOneTurnRerollTest")
            void SixtyNineTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 69
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "DIAMOND", "SWORD", "MONKEY", "SKULL", "SKULL"));
                //this will do the second reroll
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("DIAMOND", "SWORD", "PARROT", "DIAMOND", "SWORD", "DIAMOND", "SKULL", "SKULL")), "05", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("70PlayerOneTurnRerollTest")
            void SeventyTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 70
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "SWORD", "SWORD", "GOLD", "SKULL", "GOLD"));
                //this will do the second reroll
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "PARROT", "GOLD", "MONKEY", "GOLD", "SKULL", "GOLD")), "134", 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 600), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("71PlayerOneTurnRerollTest")
            void SeventyOneTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 71
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "SWORD", "SWORD", "GOLD", "SKULL", "GOLD"));
                //this will do the second reroll
                oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "PARROT", "GOLD", "MONKEY", "GOLD", "SKULL", "GOLD")), "134", 8);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }

            @Test
            @DisplayName("72PlayerOneTurnRerollTest")
            void SeventyTwoTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 72
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "GOLD", "SKULL", "MONKEY", "GOLD", "MONKEY", "MONKEY", "SKULL"));
                //this helper runs everything for us
                noReRollTest(p, setup, 7);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 600), p.getLastMessage());
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
        }
        @Nested
        @DisplayName("AcceptancePart2Tests")
        class AcceptancePartTwoTests {
            @Nested
            @DisplayName("SorceressTests")
            class SorceressTests {
                @Test
                @DisplayName("77SorceressTest")
                void SeventySevenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 77
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "PARROT", "DIAMOND", "GOLD", "DIAMOND", "PARROT", "PARROT"));
                    twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "SKULL", "DIAMOND", "GOLD", "DIAMOND", "MONKEY", "MONKEY")), new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "DIAMOND", "GOLD", "DIAMOND", "MONKEY", "MONKEY")), "267", "2", 2);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("78SorceressTest")
                void SeventyEightTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 78
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "PARROT", "SKULL", "PARROT", "SWORD", "SKULL", "PARROT"));
                    twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("SKULL", "SWORD", "PARROT", "PARROT", "PARROT", "SWORD", "SKULL", "PARROT")), new ArrayList<>(Arrays.asList("SKULL", "PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "SKULL", "PARROT")), "3", "15", 2);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1000), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("79SorceressTest")
                void SeventyNineTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 79
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "MONKEY", "PARROT", "MONKEY", "PARROT", "MONKEY", "SKULL", "PARROT"));
                    twoReRollTest(p, setup, new ArrayList<>(Arrays.asList("PARROT", "PARROT", "PARROT", "SKULL", "PARROT", "PARROT", "SKULL", "PARROT")), new ArrayList<>(Arrays.asList("PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "PARROT", "SKULL", "PARROT")), "135", "3", 2);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 2000), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Nested
            @DisplayName("MonkeyBusinessTests")
            class MonkeyBusinessTests {
                @Test
                @DisplayName("82MonkeyTest")
                void EightyTwoTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 82
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "PARROT", "MONKEY", "PARROT", "GOLD", "SKULL", "MONKEY"));
                    //this will do the second reroll
                    noReRollTest(p, setup, 9);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1100), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("83MonkeyTest")
                void EightyThreeTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 83
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "PARROT", "MONKEY", "SWORD", "GOLD", "GOLD", "SWORD"));
                    //this will do the second reroll
                    oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "PARROT", "MONKEY", "PARROT", "GOLD", "GOLD", "MONKEY")), "47",9);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1700), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("84MonkeyTest")
                void EightyFourTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 83
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "PARROT", "SKULL", "MONKEY", "PARROT", "SKULL", "MONKEY", "SKULL"));
                    //this will do the second reroll
                    noReRollTest(p, setup,9);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Nested
            @DisplayName("TreasureChestTests")
            class TreasureChestTests {
                @Test
                @DisplayName("87TreasureTest")
                void EightySevenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 83
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "PARROT", "DIAMOND", "SWORD", "PARROT", "SWORD", "DIAMOND", "GOLD"));
                    setupSinglePlayer(p, setup, Config.FORTUNE_CARDS.get(0));
                    //put 2 diamonds and 1 gold into the chest
                    assertTrue(p.addChest("276"));
                    //do reRolls
                    assertTrue(p.reRoll("35"));
                    System.out.println("================Initial reRoll:========================");
                    p.displayDice();
                    p.setDice(new ArrayList<>(Arrays.asList("PARROT", "PARROT", "DIAMOND", "PARROT", "PARROT", "PARROT", "DIAMOND", "GOLD")));
                    System.out.println("================Setup reRoll:========================");
                    p.displayDice();
                    //now put into chest again
                    assertTrue(p.addChest("01345"));
                    assertTrue(p.removeChest("276"));
                    //do reRolls
                    assertTrue(p.reRoll("276"));
                    System.out.println("================Second reRoll:========================");
                    p.displayDice();
                    p.setDice(new ArrayList<>(Arrays.asList("PARROT", "PARROT", "SKULL", "PARROT", "PARROT", "PARROT", "PARROT", "GOLD")));
                    System.out.println("================Setup reRoll:========================");
                    p.displayDice();
                    //now score
                    serverResponseDice(p, Config.FORTUNE_CARDS.get(0));
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1100), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("92TreasureTest")
                void NinetyTwoTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 83
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "PARROT", "SKULL", "GOLD", "PARROT", "GOLD", "SKULL", "GOLD"));
                    setupSinglePlayer(p, setup, Config.FORTUNE_CARDS.get(0));
                    //put 2 diamonds and 1 gold into the chest
                    assertTrue(p.addChest("357"));
                    //do reRolls
                    assertTrue(p.reRoll("014"));
                    System.out.println("================Initial reRoll:========================");
                    p.displayDice();
                    p.setDice(new ArrayList<>(Arrays.asList("GOLD", "DIAMOND", "SKULL", "GOLD", "DIAMOND", "GOLD", "SKULL", "GOLD")));
                    System.out.println("================Setup reRoll:========================");
                    p.displayDice();
                    //now put into chest again
                    assertTrue(p.addChest("0"));
                    //do reRolls
                    assertTrue(p.reRoll("14"));
                    System.out.println("================Second reRoll:========================");
                    p.displayDice();
                    p.setDice(new ArrayList<>(Arrays.asList("GOLD", "SKULL", "SKULL", "GOLD", "GOLD", "GOLD", "SKULL", "GOLD")));
                    System.out.println("================Setup reRoll:========================");
                    p.displayDice();
                    //now score
                    serverResponseDice(p, Config.FORTUNE_CARDS.get(0));
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 600), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Nested
            @DisplayName("FullChestTests")
            class FullChestTests {
                @Test
                @DisplayName("97FullTest")
                void NinetySevenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 97
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "SWORD", "MONKEY", "SWORD", "DIAMOND", "MONKEY", "PARROT"));
                    noReRollTest(p, setup,7);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 400), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("98FullTest")
                void NinetyEightTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 98
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "SWORD", "MONKEY", "GOLD", "SWORD", "MONKEY", "GOLD"));
                    noReRollTest(p, setup,1);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1800), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("99FullTest")
                void NinetyNineTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 99
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "SWORD", "MONKEY", "DIAMOND", "SWORD", "MONKEY", "SWORD"));
                    noReRollTest(p, setup,7);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1000), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("100FullTest")
                void OneHundredTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 100
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "MONKEY", "PARROT", "PARROT", "MONKEY", "GOLD"));
                    oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "MONKEY", "GOLD", "SWORD", "MONKEY", "GOLD")), "45", 4);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1200), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("103FullTest")
                void OneHundredThreeTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 103
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "GOLD", "MONKEY", "DIAMOND", "PARROT", "DIAMOND", "DIAMOND", "GOLD"));
                    noReRollTest(p, setup, 9);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1200), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Nested
            @DisplayName("SkullIslandTests")
            class SkullIslandTests {
                @Test
                @DisplayName("106SkullTest")
                void OneHundredSixTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 106
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD", "SWORD"));
                    noReRollTest(p, setup, 10);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("107SkullTest")
                void OneHundredSevenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 107
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "SKULL", "SWORD", "SWORD", "SWORD", "SWORD"));
                    noReRollTest(p, setup, 3);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("108SkullTest")
                void OneHundredEightTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 109
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "PARROT", "PARROT", "SKULL", "MONKEY", "PARROT", "MONKEY", "MONKEY"));
                    //player is in skull island, start all player's with score of 1000
                    twoReRollTest(1000, 100, p, setup, new ArrayList<>(Arrays.asList("SKULL", "SKULL", "SWORD", "SKULL", "MONKEY", "SKULL", "MONKEY", "MONKEY")), new ArrayList<>(Arrays.asList("SKULL", "SKULL", "SKULL", "SKULL", "SKULL", "SKULL", "SWORD", "SKULL")), "125", "2467", 10);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(1000, 1000), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("110SkullTest")
                void OneHundredTenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 110
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SKULL", "SKULL", "SKULL", "MONKEY", "SKULL", "MONKEY", "MONKEY"));
                    //player is in skull island, start all player's with score of 1000
                    oneReRollTest(1500, 100, p, setup, new ArrayList<>(Arrays.asList("SKULL", "SKULL", "SKULL", "SKULL", "SKULL", "SKULL", "GOLD", "SKULL")), "467", 1);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(1500, 1500), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("111SkullTest")
                void OneHundredElevenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 111
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "SKULL", "SWORD", "SWORD", "SWORD", "SWORD"));
                    //player is in skull island, start all player's with score of 1000
                    oneReRollTest(600, 100, p, setup, new ArrayList<>(Arrays.asList("SKULL", "GOLD", "SKULL", "SKULL", "GOLD", "GOLD", "GOLD", "GOLD")), "17654", 10);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(600, 600), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Nested
            @DisplayName("SeaBattleTests")
            class SeaBattleTests {
                @Test
                @DisplayName("114BattleTest")
                void OneHundredFourteenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 114
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "MONKEY", "SKULL", "SKULL", "MONKEY", "MONKEY", "MONKEY", "SWORD"));
                    //player is in skull island, start all player's with score of 1000
                    noReRollTest(300, 0, p, setup, 4);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(300, 0), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("115BattleTest")
                void OneHundredFifteenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 115
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "PARROT", "PARROT", "SWORD", "PARROT", "PARROT"));
                    //player is in skull island, start all player's with score of 1000
                    oneReRollTest(500, 0, p, setup, new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "SKULL", "SKULL", "SWORD", "SKULL", "SKULL")), "3467", 5);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(500, 0), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("116BattleTest")
                void OneHundredSixteenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 116
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SKULL", "MONKEY", "MONKEY", "SWORD", "SKULL", "SWORD"));
                    //player is in skull island, start all player's with score of 1000
                    noReRollTest(1000, 0, p, setup, 6);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(1000, 0), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("117BattleTest")
                void OneHundredSeventeenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 117
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SWORD", "MONKEY", "MONKEY", "MONKEY", "SWORD", "PARROT", "GOLD"));
                    //player is in skull island, start all player's with score of 1000
                    noReRollTest( p, setup, 4);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
                @Test
                @DisplayName("118BattleTest")
                void OneHundredEighteenTest() {
                    Player p = new Player(Config.PLAYER_PORT_NUMBER);
                    //setup according to line 118
                    ArrayList<String> setup = new ArrayList<>(Arrays.asList("PARROT", "SWORD", "MONKEY", "MONKEY", "MONKEY", "SKULL", "PARROT", "MONKEY"));
                    //player is in skull island, start all player's with score of 1000
                    oneReRollTest(p, setup, new ArrayList<>(Arrays.asList("SWORD", "SWORD", "MONKEY", "MONKEY", "MONKEY", "SKULL", "SKULL", "MONKEY")), "06", 4);
                    //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                    assertEquals(Config.SERVER_SCORE_MESSAGE(0, 500), p.getLastMessage());
                    //asserts on each player's score are outside done in functions called by twoReRollTest
                    System.out.println(p.getLastMessage());
                    //shouldn't be the player's turn anymore
                    assertFalse(p.getTurn());
                    //teardown
                    p.close();
                }
            }
            @Test
            @DisplayName("120BattleTest")
            void OneHundredTwentyTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 120
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SWORD", "SWORD", "MONKEY", "MONKEY", "SWORD", "SKULL", "SWORD", "MONKEY"));
                //player is in skull island, start all player's with score of 1000
                noReRollTest(p, setup, 5);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 800), p.getLastMessage());
                //asserts on each player's score are outside done in functions called by twoReRollTest
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
            @Test
            @DisplayName("121BattleTest")
            void OneHundredTwentyOneTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 121
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "MONKEY", "SKULL", "SKULL", "SWORD", "MONKEY"));
                //player is in skull island, start all player's with score of 1000
                oneReRollTest(500, 0, p, setup, new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "SKULL", "SKULL", "SKULL", "SWORD", "SWORD")), "0237", 5);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(500, 0), p.getLastMessage());
                //asserts on each player's score are outside done in functions called by twoReRollTest
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
            @Test
            @DisplayName("123BattleTest")
            void OneHundredTwentyThreeTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 123
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("MONKEY", "SWORD", "MONKEY", "MONKEY", "SWORD", "SWORD", "SWORD", "SKULL"));
                //player is in skull island, start all player's with score of 1000
                noReRollTest(p, setup, 6);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1300), p.getLastMessage());
                //asserts on each player's score are outside done in functions called by twoReRollTest
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
            @Test
            @DisplayName("124BattleTest")
            void OneHundredTwentyFourTest() {
                Player p = new Player(Config.PLAYER_PORT_NUMBER);
                //setup according to line 124
                ArrayList<String> setup = new ArrayList<>(Arrays.asList("SKULL", "PARROT", "PARROT", "DIAMOND", "SWORD", "MONKEY", "MONKEY", "MONKEY"));
                //player is in skull island, start all player's with score of 1000
                twoReRollTest( p, setup, new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "DIAMOND", "SWORD", "MONKEY", "MONKEY", "MONKEY")), new ArrayList<>(Arrays.asList("SKULL", "SWORD", "SWORD", "DIAMOND", "SWORD", "PARROT", "SWORD", "PARROT")), "12", "567", 6);
                //Server Score message is the word response the server gives with a given initial and final score, which should be zero since we rerolled and had three skulls
                assertEquals(Config.SERVER_SCORE_MESSAGE(0, 1300), p.getLastMessage());
                //asserts on each player's score are outside done in functions called by twoReRollTest
                System.out.println(p.getLastMessage());
                //shouldn't be the player's turn anymore
                assertFalse(p.getTurn());
                //teardown
                p.close();
            }
        }
    }

    //Helpers

    void noReRollTest(Player player, ArrayList<String> setup, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //now simulate server response and endturn
        serverResponseDice(player, Config.FORTUNE_CARDS.get(fc));
    }

    void noReRollTest(int initial, int end, Player player, ArrayList<String> setup, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //now simulate server response and endturn
        serverResponseDice(initial, end, player, Config.FORTUNE_CARDS.get(fc));
    }

    void oneReRollTest(Player player, ArrayList<String> setup, ArrayList<String> setupTwo, String reRoll, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //do reRolls (SWORDS)
        assertTrue(player.reRoll(reRoll));
        System.out.println("================Initial reRoll:========================");
        player.displayDice();
        player.setDice(setupTwo);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //now simulate server response and endturn
        serverResponseDice(player, Config.FORTUNE_CARDS.get(fc));
    }

    void oneReRollTest(int initial, int end, Player player, ArrayList<String> setup, ArrayList<String> setupTwo, String reRoll, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //do reRolls (SWORDS)
        assertTrue(player.reRoll(reRoll));
        System.out.println("================Initial reRoll:========================");
        player.displayDice();
        player.setDice(setupTwo);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //now simulate server response and endturn
        serverResponseDice(initial, end, player, Config.FORTUNE_CARDS.get(fc));
    }

    void twoReRollTest(Player player, ArrayList<String> setup, ArrayList<String> setupTwo, ArrayList<String> setupThree, String reRoll, String reRollTwo, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //do reRolls
        assertTrue(player.reRoll(reRoll));
        System.out.println("================Initial reRoll:========================");
        player.displayDice();
        player.setDice(setupTwo);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //do reRolls
        assertTrue(player.reRoll(reRollTwo));
        System.out.println("================Second reRoll:========================");
        player.displayDice();
        player.setDice(setupThree);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //now simulate server response and endturn
        serverResponseDice(player, Config.FORTUNE_CARDS.get(fc));
    }

    void twoReRollTest(int initial, int end, Player player, ArrayList<String> setup, ArrayList<String> setupTwo, ArrayList<String> setupThree, String reRoll, String reRollTwo, int fc) {
        setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
        //do reRolls
        assertTrue(player.reRoll(reRoll));
        System.out.println("================Initial reRoll:========================");
        player.displayDice();
        player.setDice(setupTwo);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //do reRolls
        assertTrue(player.reRoll(reRollTwo));
        System.out.println("================Second reRoll:========================");
        player.displayDice();
        player.setDice(setupThree);
        System.out.println("================Setup reRoll:========================");
        player.displayDice();
        //now simulate server response and endturn
        serverResponseDice(initial, end, player, Config.FORTUNE_CARDS.get(fc));

    }

    void setupSinglePlayer(Player p, ArrayList<String> dice, String fc) {
        p.setTurn(true);
        p.setFortune(fc);
        System.out.println("Initial roll:");
        //preventing possible skull island by convincing the player it isn't their first turn.
        p.setDice(new ArrayList<>());
        p.rollDice();
        p.displayDice();
        p.setDice(new ArrayList<>());
        p.setDice(dice);
        System.out.println("Setup:");
        p.displayDice();
    }

    void serverResponseDice(Player p, String fc) {
        Game g = new Game();
        Server s = new Server(g);
        g.setFortune(fc, 1);
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
        //teardown
        datagramTeardown(sendSocket, sendPacket);
        s.close();
    }

    void serverResponseDice(int initial, int end, Player p, String fc) {
        Game g = new Game();
        Server s = new Server(g);
        g.setFortune(fc, 1);
        g.setScores(initial);
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
        if (g.getFortune(1) == 4 || g.getFortune(1) == 5 || g.getFortune(1) == 6) {
            //basically if we have a sea battle...
            assertEquals(g.getScores()[0], end);
            assertEquals(g.getScores()[1], initial);
            assertEquals(g.getScores()[2], initial);
        }else {
            assertEquals(g.getScores()[0], initial);
            assertEquals(g.getScores()[1], end);
            assertEquals(g.getScores()[2], end);
        }
        //teardown
        datagramTeardown(sendSocket, sendPacket);
        s.close();
    }

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
