package grader.model;

import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer");
    }

    // -------------------------------
    // 1. Initial State
    // -------------------------------
    @Test
    void testInitialValues() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(Player.getInitialMoney(), player.getMoney());
        assertEquals(0, player.getPosition());
        assertFalse(player.isInJail());
        assertEquals(0, player.getSkipTurns());
        assertEquals(0, player.getJailTurns());
    }

    // -------------------------------
    // 2. Money management
    // -------------------------------
    @Test
    void testAddMoney() {
        player.addMoney(200);
        assertEquals(Player.getInitialMoney() + 200, player.getMoney());
    }

    @Test
    void testDeductMoneyEnough() {
        boolean result = player.deductMoney(300);

        assertTrue(result);
        assertEquals(Player.getInitialMoney() - 300, player.getMoney());
    }

    @Test
    void testDeductMoneyNotEnough() {
        int initial = player.getMoney();
        boolean result = player.deductMoney(initial + 100);

        assertFalse(result);
        assertEquals(-100, player.getMoney());
    }

    // -------------------------------
    // 3. Movement
    // -------------------------------
    @Test
    void testMoveNormal() {
        player.move(3);
        assertEquals(3, player.getPosition());
    }

    @Test
    void testMoveWrapAroundBoard() {
        player.move(19);
        assertEquals(19, player.getPosition());

        player.move(2); // 19 + 2 = 21 -> 1
        assertEquals(1, player.getPosition());
    }

    // -------------------------------
    // 4. Passing GO
    // -------------------------------
    @Test
    void testPassGoGetMoney() {
        player.warp(18);
        int initialMoney = player.getMoney();

        player.move(4); // 18 + 4 = 22 -> pass GO
        assertEquals(2, player.getPosition());
        assertEquals(initialMoney + 20, player.getMoney());
    }

    // -------------------------------
    // 5. Jail logic
    // -------------------------------
    @Test
    void testGoToJail() {
        player.goToJail();

        assertTrue(player.isInJail());
        assertEquals(5, player.getPosition());
        assertEquals(2, player.getJailTurns());
    }

    @Test
    void testReleaseFromJail() {
        player.goToJail();
        player.releaseFromJail();

        assertFalse(player.isInJail());
        assertEquals(0, player.getJailTurns());
    }

    @Test
    void testDecrementJailTurns() {
        player.goToJail();

        player.decrementJailTurns();
        assertEquals(1, player.getJailTurns());

        player.decrementJailTurns();
        assertEquals(0, player.getJailTurns());

        // ไม่ควรติดลบ
        player.decrementJailTurns();
        assertEquals(0, player.getJailTurns());
    }

    // -------------------------------
    // 6. Warp
    // -------------------------------
    @Test
    void testWarp() {
        player.warp(10);
        assertEquals(10, player.getPosition());
    }
}
