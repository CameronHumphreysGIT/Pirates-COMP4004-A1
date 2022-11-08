import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;

public class SinglePlayerStepDefs {
    Player p;
    ArrayList<String> setup;

    @Given("The Player has been setup as the first player")
    public void the_player_has_been_setup_as_the_first_player() {
        p = new Player(Config.PLAYER_PORT_NUMBER);
    }

    @When("NoReRollTest is run with {string}, {string}, {string}, {string}, {string}, {string}, {string}, {string} and {int}")
    public void no_re_roll_test_is_run_with_and(String dice1, String dice2, String dice3, String dice4, String dice5, String dice6, String dice7, String dice8, Integer fc) {
        //ik this looks done, but it radically helps my ability to quickly add to the data table in the scenario
        setup = new ArrayList<>();
        setup.add(dice1);
        setup.add(dice2);
        setup.add(dice3);
        setup.add(dice4);
        setup.add(dice5);
        setup.add(dice6);
        setup.add(dice7);
        setup.add(dice8);
        Tester test = new Tester();
        test.noReRollTest(p, setup, fc);
    }
    @Then("The player's last message is {int}")
    public void the_player_s_last_message_is(Integer finalScore) {
        Assert.assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, finalScore), p.getLastMessage());
    }
}
