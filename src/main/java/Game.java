public class Game {
    private int playerCount;
    private int currentTurn;
    private int[] scores = new int[3];

    public int getPlayerCount() {
        return playerCount;
    }

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
}
