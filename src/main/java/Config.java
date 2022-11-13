import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Config {

    public static final int SERVER_PORT_NUMBER = 5000;
    //player_port_number is for testing
    public static int PLAYER_PORT_NUMBER = 3010;
    public static final int MAX_PLAYERS = 3;
    public static final int TIMEOUT = 5000; //5 seconds
    public static final int WIN_SCORE = 3000;
    public static String SERVER_JOIN_MESSAGE(int i) {
        return "You are Player" + i;
    }
    public static String SERVER_SCORE_MESSAGE(int before, int after) {
        if (before > after) {
            return "Your score was " + before + " and you LOST " + (before-after) + " so your score is now: " + after;
        }
        return "Your score was " + before + " and you scored " + (after-before) + " so your score is now: " + after;
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
    public static final String FINALTURN =
            "|=====================================================================|\n" +
            "|  GAME ENDING                                                        |\n" +
            "|                                                                     |\n" +
            "|  Your next Turn may be your last.                                   |\n" +
            "|  One of your opponents has reached the win threshold (" + WIN_SCORE + "pts)      |\n" +
            "|  You have one more turn, the player with the highest score will win,|\n" +
            "|  If all players have less then the threshold, the game will continue|\n" +
            "|=====================================================================|\n";
    public static String WINNER(int winner) {
        return
                "|=====================================================================|\n" +
                "|  GAME OVER                                                          |\n" +
                "|                                                                     |\n" +
                "|  CONGRATULATIONS Player" + winner + ", You've won                               |\n" +
                "|  I hope you've enjoyed PIRATES                                      |\n" +
                "|  I'll see you on the seven seas                                     |\n" +
                "|=====================================================================|\n";
    }

    public static final ArrayList<String> DICE = new ArrayList<>(Arrays.asList("SKULL", "SWORD", "MONKEY", "PARROT", "DIAMOND", "GOLD"));
    //awkwardly placed SKULL1 is due to the fact that there is no seabattle1...
    public static final ArrayList<String> FORTUNE_CARDS = new ArrayList<>(Arrays.asList("TREASURE", "CAPTAIN", "SORCERESS", "SKULL1", "SEABATTLE2", "SEABATTLE3", "SEABATTLE4", "GOLD", "DIAMOND","MONKEY", "SKULL2"));
    public static String FORTUNE_DESCRIPTION(String fc) {
        switch (fc) {
            case "TREASURE":
                return "In the Treasure Chest you may protect your fortune.\n" +
                        "After each roll you may place (or take out) any die that you decide to keep on the Treasure Chest card.\n" +
                        "If you are disqualified, you still score the points for the dice that you have placed on the card.\n";
            case "CAPTAIN":
                return  "The score you make this turn is doubled.\n" +
                        "If you go to the Island of the Dead, each player will lose 200 points for each skull (instead of 100 points).\n";
            case "SORCERESS":
                return "The sorceress brings back to life one skull and allows you to re-roll one skull.\n" +
                        "(The spell is good for one time only).\n";
            case "GOLD":
                return "You start your turn with one gold coin. It is counted for its face value as well as for a set.";
            case "DIAMOND":
                return "You start your turn with one diamond. It is counted for its face value as well as for a set.";
            case "MONKEY":
                return "The Monkeys and the Parrots you roll are\n" +
                        "grouped together and are considered as one group for making a\n" +
                        "set. (Example: 2 Parrots and 3 Monkeys are considered as “five of a kind”).\n";
        }
        if (fc.substring(0, 5).equals("SKULL")) {
            return "You start your turn with the number of skulls that appear on\n" +
                    "the card.\n";
        }
        if (fc.substring(0, 9).equals("SEABATTLE")) {
            return "Your ship is engaged in a sea battle. To win, you must\n" +
                    "get the indicated number of swords. If you make it, you get the indicated bonus in addition to your score. If you fail, however, your dice\n" +
                    "are ignored and you lose the indicated bonus points. A player who is\n" +
                    "engaged in a sea battle cannot go to the Island of the Dead.\n";
        }
        return "";
    }
    public static int[] SEABATTLE_BONUS = {300, 500, 1000};
}
