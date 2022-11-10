import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerStepDefs {
    Player p;
    ArrayList<String> setup;
    ArrayList<String> reRoll;
    int first = 0;
    int other = 0;

    @Given("The Player has been setup as the first player")
    public void the_player_has_been_setup_as_the_first_player() {
        p = new Player(Config.PLAYER_PORT_NUMBER);
    }

    @And("Setup is made with dice")
    public void setupIsMadeWithDice(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        //should only be one row...
        setup = new ArrayList<String>(rows.get(0));
    }

    @And("the first and final scores for other players have been set with {int} and {int}")
    public void theFirstAndFinalScoresForOtherPlayersHaveBeenSetWithFirstScoreAndOtherScore(Integer firstS, Integer finalS) {
        first = firstS;
        other = finalS;
    }

    @When("NorerollTest is run with {int}")
    public void norerolltestIsRunWithFortuneCard(Integer fc) {
        Tester test = new Tester();
        test.noReRollTest(p, setup, fc);
    }

    @Then("The player's last message is {int}")
    public void the_player_s_last_message_is(Integer finalScore) {
        if (finalScore == 0) {
            Assert.assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(0, 0), p.getLastMessage());
        }else {
            Assert.assertEquals(Config.SERVER_SCORE_MESSAGE(0, finalScore), p.getLastMessage());
        }
    }

    @Then("The player's last message is {int} {int}")
    public void the_player_s_last_message_is(Integer firstScore, Integer finalScore) {
        if (finalScore.equals(firstScore)) {
            Assert.assertEquals("YOU'VE DIED " + Config.SERVER_SCORE_MESSAGE(firstScore, finalScore), p.getLastMessage());
        }else {
            Assert.assertEquals(Config.SERVER_SCORE_MESSAGE(firstScore, finalScore), p.getLastMessage());
        }
    }

    @And("The player socket is closed.")
    public void thePlayerSocketIsClosed() {
        p.close();
    }

    @And("Initial and Reroll are setup with dice")
    public void initialAndRerollAreSetupWithDice(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        //should only be two rows...
        setup = new ArrayList<String>(rows.get(0));
        reRoll = new ArrayList<String>(rows.get(1));
    }

    @When("onererollTest is run with {int} and {string}")
    public void onererolltestIsRunWithFortuneCardAndReRoll(Integer fc, String rr) {
        Tester test = new Tester();
        if (first != 0) {
            test.oneReRollTest(first, other, p, setup, reRoll, rr, fc);
        } else {
            test.oneReRollTest(p, setup, reRoll, rr, fc);
        }
    }
}
