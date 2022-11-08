import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private int playerCount;
    private int currentTurn;
    private int[] scores = new int[3];
    private int[] fortunes = new int[3];
    private ArrayList<Integer> deck = new ArrayList<>();
    private int round = 0;

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
        //setupDeck(2);
        //setupDeck(0);
        for (int i = 0; i < playerCount; i++) {
            //get the fortune and remove it.
            fortunes[i] = deck.get(0);
            deck.remove(0);
        }
    }

    public boolean score(String dice, int player) {
        try {
            if (Integer.parseInt("" + dice.charAt(6)) == 0 && (fortunes[player - 1] != 0)) {
                //this means we are in skull island, score differently.
                //this func will score for skull island, give it the amount of skulls and the player num
                skullScore(Integer.parseInt("" + dice.charAt(0)), player);
                return true;
            }
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        //check validity
        if (dice.length() > 6 &&(fortunes[player - 1] != 0)) {
            //in treasure chest, the contents are appended to the dice string
            return false;
        }
        int sum = 0;
        int[] diceCount = new int[6];
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
        try {
            if (Config.FORTUNE_CARDS.get(fortunes[player - 1]).substring(0, 5).equals("SKULL")) {
                //basically, take the end of the fortune card name (either 1 or 2) and add it to the amount of skulls in the count
                diceCount[0] += Integer.parseInt(Config.FORTUNE_CARDS.get(fortunes[player - 1]).charAt(5) + "");
            }
        } catch (StringIndexOutOfBoundsException ignored) {
        }

        if (diceCount[0] >= 3) {
            if (fortunes[player - 1] == 0) {
                //they got the treasure chest and died
                //empty the diceCount, keep the skulls to avoid fullChest buggyness
                diceCount = new int[]{3, 0, 0, 0, 0, 0};
                //add all the chest dice
                for (int i = 6; i < dice.length(); i++) {
                    //looks weird, just getting the dice from the chest and adding to the diceCount
                    diceCount[Integer.parseInt(dice.charAt(i) + "")]++;
                }
            }else if (fortunes[player - 1] == 4 || fortunes[player - 1] == 5 || fortunes[player - 1] == 6) {
                //deduct score
                // we lost the battle even if we have enough swords
                scores[player - 1] -=  Config.SEABATTLE_BONUS[fortunes[player - 1] - 4];
                if (scores[player - 1] < 0) {
                    scores[player - 1] = 0;
                }
                return true;
            } else {
                //There are exactly three skulls
                //don't change the score.
                scores[player - 1] += 0;
                return true;
            }
        }
        if (fortunes[player - 1] == 7) {
            //increment the amount of gold
            diceCount[5]++;
        }
        if (fortunes[player - 1] == 8) {
            //increment the amount of diamonds
            diceCount[4]++;
        }
        if (fortunes[player - 1] == 9) {
            //monkey buisiness...
            //treat as though all parrots are monkeys
            diceCount[2] += diceCount[3];
            diceCount[3] = 0;
        }
        int score = 0;
        //only score for non skulls (start at 1)
        for (int i = 1; i < 6; i++) {
            //score for each combination
            switch (diceCount[i]) {
                //case 9 is for diamond and gold possible combinations
                case 9:
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
        //check if there's a full chest
        boolean full = true;
        //loop through sword, parrot and monkey, gold and diamond score no matter what
        for (int i = 1; i < 4; i++) {
            if (i == 1 && (fortunes[player - 1] == 4 || fortunes[player - 1] == 5 || fortunes[player - 1] == 6)) {
                //we know the sword amount, and we are sea battle 2
                if (diceCount[1] >= (fortunes[player - 1] - 2)) {
                    score += Config.SEABATTLE_BONUS[fortunes[player - 1] - 4];
                    //this also means we scored with those two dice
                }else {
                    //can't possibly be full
                    score -= Config.SEABATTLE_BONUS[fortunes[player - 1] - 4];
                    full = false;
                    break;
                }
            } else if (diceCount[i] < 3 && diceCount[i] != 0) {
                full = false;
                break;
            }
        }
        if (full && diceCount[0] == 0) {
            //fullchest also needs to have 0 skulls
            score += 500;
        }
        //score for gold and diamond
        score += 100 * (diceCount[4] + diceCount[5]);
        if (fortunes[player - 1] == 1) {
            //captain, double score
            scores[player - 1] += score;
        }
        scores[player - 1] += score;
        if (scores[player - 1] < 0) {
            scores[player - 1] = 0;
        }
        nextTurn();
        return true;
    }

    public void skullScore(int skulls, int playerNum) {
        if (fortunes[playerNum - 1] == 3) {
            skulls ++;
        }
        if (fortunes[playerNum - 1] == 10) {
            skulls ++;
            skulls ++;
        }
        if (fortunes[playerNum - 1] == 1) {
            skulls = skulls * 2;
        }
        for (int i = 0; i < scores.length; i++) {
            if (i != playerNum - 1) {
                scores[i] -= skulls * 100;
                if (scores[i] < 0) {
                    scores[i] = 0;
                }
            }
        }
    }

    public void nextTurn() {
        if (currentTurn == playerCount) {
            //next turn goes to the first player
            currentTurn = 1;
            //we completed a round
            round++;
        }else {
            currentTurn++;
        }
    }

    public void setupDeck() {
        //there are 35 cards, but we don't have a specific knowledge about the compisition:
        //my comp: 4xTreasure, Captain, Sorceress, and Monkey(16), 5x gold and diamond (10), 2xseabattles2 and 3 and 1x4 2xskulls (9)
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
            //seabattles
            deck.add(4);
            deck.add(5);
            //skulls
            deck.add(3);
            deck.add(10);
        }
        //seabattle4
        deck.add(6);
        //now shuffle
        Collections.shuffle(deck);
    }

    public void setupDeck(int card) {
        //rigging for testing, set whole deck to be one card
        for (int i = 0; i < 35; i++) {
            deck.add(card);
        }
    }

    public void setScores(int score) {
        for (int i = 0; i < scores.length; i++) {
            scores[i] = score;
        }
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

    public int getRound() {
        return round;
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
