import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Tester {
    @org.junit.jupiter.api.Test
    @DisplayName("NetworkingTests")
    void NetworkingTests() {
        Player p = new Player();
        Server s = new Server();
        String message = "helloPlayer1";
        s.receive(message);
        assertEquals(message, p.rpc_send("Hello"));
    }
}
