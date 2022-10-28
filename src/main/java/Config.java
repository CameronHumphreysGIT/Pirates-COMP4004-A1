import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {

    public static final int SERVER_PORT_NUMBER = 5000;
    public static int PLAYER_PORT_NUMBER(int i) {
        //3010 for player 1... and so on
        return 3009 + i;
    }
    public static final int MAX_PLAYERS = 3;
    public static final int TIMEOUT = 5000; //5 seconds
    public static String SERVER_JOIN_MESSAGE(int i) {
        return "You are Player" + i;
    }
    public static final String PLAYER_JOIN_MESSAGE = "Join Request";
    public static final Logger LOGGER = LogManager.getLogger(Config.class);
    public static final String WELCOME =
            "|=====================================================================|\n" +
            "|  WELCOME TO PIRATES                                                 |\n" +
            "|                                                                     |\n" +
            "|  PIRATES is a game of rolling and re-rolling a set of 8 dice        |\n" +
            "|  while avoiding the deadly Skulls                                   |\n" +
            "|  if you roll 3 skulls, your turn ends                               |\n" +
            "|=====================================================================|\n";
}
