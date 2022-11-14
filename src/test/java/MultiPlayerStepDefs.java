import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiPlayerStepDefs {
    Server s;
    Game g;
    Player p1;
    Player p2;
    Player p3;


    @Given("The Server has been created")
    public void theServerHasBeenCreated() {
        g = new Game();
        s = new Server(g);
    }

    @And("Each player has been created")
    public void eachPlayerHasBeenCreated() {
        p1 = new Player();
        p2 = new Player();
        p3 = new Player();
    }

    @And("Each player subsequently joins the game")
    public void eachPlayerSubsequentlyJoinsTheGame() {
        //order matters here
        joinServer(s, p1);
        System.out.println(s.playerPorts.get(0));
        //also we are joining from the same port which is a redundency i check for...
        //let's game the system.
        s.playerPorts = new ArrayList<>(Arrays.asList(s.playerPorts.get(0), s.playerPorts.get(0), s.playerPorts.get(0)));
        joinServer(s, p2);
        //set numbers because networking doesn't work properly since p1 p2 and p3 use the same ports... not good
        p2.setNumber(2);
        joinServer(s, p3);
        p3.setNumber(3);
        //start the game
        g.start();
    }

    @When("Each Player has a fortune card and rolls")
    public void eachPlayerHasAFortuneCardAndRolls(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        //copying code bad... but i want to actually modify our server and game objects...
        noReRollTest(p1, new ArrayList<String>(Arrays.asList(rows.get(0).get(2).split(","))), Integer.parseInt(rows.get(0).get(1)));
        noReRollTest(p2, new ArrayList<String>(Arrays.asList(rows.get(1).get(2).split(","))), Integer.parseInt(rows.get(1).get(1)));
        noReRollTest(p3, new ArrayList<String>(Arrays.asList(rows.get(2).get(2).split(","))), Integer.parseInt(rows.get(2).get(1)));
    }

    @And("The first player rolls again")
    public void theFirstPlayerRollsAgain(DataTable table) {
        //addition for line 142 test
        List<List<String>> rows = table.asLists(String.class);
        noReRollTest(p1, new ArrayList<String>(Arrays.asList(rows.get(0).get(2).split(","))), Integer.parseInt(rows.get(0).get(1)));
    }

    @And("The second player rolls and rerolls")
    public void theSecondPlayerRollsAndRerolls(DataTable table) {
        //addition for line 147 test
        List<List<String>> rows = table.asLists(String.class);
        twoReRollTest(p2, new ArrayList<String>(Arrays.asList(rows.get(0).get(2).split(","))), new ArrayList<String>(Arrays.asList(rows.get(0).get(3).split(","))),
                new ArrayList<String>(Arrays.asList(rows.get(0).get(4).split(","))), rows.get(0).get(5), rows.get(0).get(6),Integer.parseInt(rows.get(0).get(1)));
    }

    @And("The game is ended")
    public void theGameIsEnded() {
        //finally, call endGame from the server.
        s.EndGame();
        //once again, networking is broken since the three players are the same
        p1.setLastMessage(s.getLastMessage());
        p2.setLastMessage(s.getLastMessage());
        p3.setLastMessage(s.getLastMessage());
    }

    @Then("PlayerOne's Score is {int}")
    public void playeroneSScoreIs(int score) {
        Assert.assertEquals(score, g.getScores()[0]);
    }

    @And("PlayerTwo's Score is {int}")
    public void playertwoSScoreIs(int score) {
        Assert.assertEquals(score, g.getScores()[1]);
    }

    @And("PlayerThree's Score is {int}")
    public void playerthreeSScoreIs(int score) {
        Assert.assertEquals(score, g.getScores()[2]);
    }

    @And("Each player's last Message is {string}")
    public void eachPlayerSLastMessageIs(String lastMes) {
        Assert.assertEquals(p1.getLastMessage(), lastMes);
        Assert.assertEquals(p2.getLastMessage(), lastMes);
        Assert.assertEquals(p3.getLastMessage(), lastMes);
    }

    public void joinServer(Server s, Player p) {
        //rpc_send join message from player.
        p.rpc_send("Join Request");
        //call add_player from server.
        s.addPlayer();
        //call player.join
        p.join();
    }

    void noReRollTest(Player p, ArrayList<String> setup, int fc) {
        Tester test = new Tester();
        test.setupSinglePlayer(p, setup, Config.FORTUNE_CARDS.get(fc));
        //now simulate server response and endturn
        g.setFortune(Config.FORTUNE_CARDS.get(fc), p.getNumber());
        //let's simulate the server to send the response
        DatagramSocket sendSocket = test.setupSocket(false, true);
        //scorePlayer will send a nice response for the player
        byte msg[] = s.scorePlayer(p.getDiceString(), p.getNumber()).getBytes();
        DatagramPacket sendPacket = test.setupPacket(msg, false, false);
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        p.endTurn();
        //teardown
        test.datagramTeardown(sendSocket, sendPacket);
    }

    void twoReRollTest(Player player, ArrayList<String> setup, ArrayList<String> setupTwo, ArrayList<String> setupThree, String reRoll, String reRollTwo, int fc) {
        Tester test = new Tester();
        test.setupSinglePlayer(player, setup, Config.FORTUNE_CARDS.get(fc));
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
        g.setFortune(Config.FORTUNE_CARDS.get(fc), player.getNumber());
        //let's simulate the server to send the response
        DatagramSocket sendSocket = test.setupSocket(false, true);
        //scorePlayer will send a nice response for the player
        byte msg[] = s.scorePlayer(player.getDiceString(), player.getNumber()).getBytes();
        DatagramPacket sendPacket = test.setupPacket(msg, false, false);
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        player.endTurn();
        //teardown
        test.datagramTeardown(sendSocket, sendPacket);
    }
}
