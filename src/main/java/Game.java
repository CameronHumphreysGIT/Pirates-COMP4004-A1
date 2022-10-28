public class Game {
    private int playerCount;
    private int currentTurn;
    private int[] scores = new int[3];

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
    }

    public boolean score(String dice, int player) {
        //check validity
        if (dice.length() > 6) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 6; i++) {
            //parse the int at each index
            try {
                sum += Integer.parseInt("" + dice.charAt(i));
            } catch (NumberFormatException e){
                //happens if any character is not an int
                return false;
            }
        }
        if (sum != 8) {
            return false;
        }
        //non error
        if (Integer.parseInt("" + dice.charAt(0)) == 3) {
            //There are exactly three skulls
            //don't change the score.
            scores[player - 1] += 0;
        }
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
