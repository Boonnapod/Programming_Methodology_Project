package grader.model.square;

import model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GoToJailSquareTest {

    @Test
    void testLandOn_SendsPlayerToJail() {
        // Setup
        GoToJailSquare jailSquare = new GoToJailSquare(15);
        Player player = new Player("Bad Guy");

        // Action
        jailSquare.landOn(player);

        // Assert: เช็คว่าสถานะของ Player เปลี่ยนเป็นติดคุกจริงไหม
        assertTrue(player.isInJail(), "Player should be in jail after landing on GoToJailSquare");
    }
}
