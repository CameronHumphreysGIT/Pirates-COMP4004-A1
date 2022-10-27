public class Config {

    public static final int SERVER_PORT_NUMBER = 5000;
    public static final int PLAYER_PORT_NUMBER = 3010;
    public static final int NUM_OF_PLAYERS = 3;
    public static final int TIMEOUT = 5000; //5 seconds
    public static String SERVER_JOIN_MESSAGE(int i) {
        return "You are Player" + i;
    }
    public static final String PLAYER_JOIN_MESSAGE = "Join Request";

}
