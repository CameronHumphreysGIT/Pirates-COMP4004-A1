import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private int playerCount;
    private int currentTurn;
    private int[] scores = new int[3];
    private int[] fortunes = new int[3];
    private ArrayList<Integer> deck = new ArrayList<>();

    public void addPlayer() {
        playerCount++;
    }

    public void start() {
        //start the game
        currentTurn = 1;
        //set scores
        for (int i = 0; i < playerCount; i++) {
            scores[i] = 0;
        }
        //set fortune cards
        setupDeck();
        for (int i = 0; i < playerCount; i++) {
            //get the fortune and remove it.
            fortunes[i] = deck.get(0);
            deck.remove(0);
        }
    }

    public boolean score(String dice, int player) {
        //check validity
        if (dice.length() > 6) {
            return false;
        }
        int sum = 0;
        int[] diceCount = new int[8];
        for (int i = 0; i < 6; i++) {
            //parse the int at each index
            try {
                int currentCount = Integer.parseInt("" + dice.charAt(i));
                sum += currentCount;
                diceCount[i] = currentCount;
            } catch (NumberFormatException e){
                //happens if any character is not an int
                return false;
            }
        }
        if (sum != 8) {
            return false;
        }
        //non error
        if (diceCount[0] == 3) {
            //There are exactly three skulls
            //don't change the score.
            scores[player - 1] += 0;
            return true;
        }
        if (fortunes[player - 1] == 7) {
            //increment the amount of gold
            diceCount[5]++;
        }
        int score = 0;
        //only score for non skulls (start at 1)
        for (int i = 1; i < 6; i++) {
            //score for each combination
            switch (diceCount[i]) {
                case 8:
                    score += 4000;
                    break;
                case 7:
                    score += 2000;
                    break;
                case 6:
                    score += 1000;
                    break;
                case 5:
                    score += 500;
                    break;
                case 4:
                    score += 200;
                    break;
                case 3:
                    score += 100;
            }
        }
        //score for gold and diamond
        score += 100 * (diceCount[4] + diceCount[5]);
        if (fortunes[player - 1] == 1) {
            //captain, double score
            scores[player - 1] += score;
        }
        scores[player - 1] += score;
        nextTurn();
        return true;
    }

    public void nextTurn() {
        if (currentTurn == playerCount) {
            //next turn goes to the first player
            currentTurn = 1;
        }else {
            currentTurn++;
        }
    }

    public void setupDeck() {
        //there are 35 cards, but we don't have a specific knowledge about the compisition:
        //my comp: 4xTreasure, Captain, Sorceress, and Monkey(16), 5x gold and diamond (10), 2xseabattles1, 1x2, 3 and 4 2xskulls (9)
        for (int i = 0; i < 4; i++) {
            //treasure
            deck.add(0);
            //Captain
            deck.add(1);
            //Sorceress
            deck.add(2);
            //Monkey
            deck.add(9);
        }
        for (int i = 0; i < 5; i++) {
            //gold
            deck.add(7);
            //diamond
            deck.add(8);
        }
        for (int i = 0; i < 2; i++) {
            //seabattle1
            deck.add(3);
            //skulls
            deck.add(10);
            deck.add(11);
        }
        //seabattles
        deck.add(4);
        deck.add(5);
        deck.add(6);
        //now shuffle
        Collections.shuffle(deck);
    }

    public boolean setFortune(String fc, int player) {
        int index = Config.FORTUNE_CARDS.indexOf(fc);
        if (index != -1) {
            //fortunes stores the indices of the Fortune Card in question
            fortunes[player - 1] = index;
            return true;
        }else {
            return false;
        }
    }

    public int getFortune(int playerNum) {
        return fortunes[playerNum - 1];
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int[] getScores() {
        return scores;
    }
}
