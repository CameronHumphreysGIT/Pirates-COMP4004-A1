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

    @And("The player socket is closed.")
    public void thePlayerSocketIsClosed() {
        p.close();
    }
}
