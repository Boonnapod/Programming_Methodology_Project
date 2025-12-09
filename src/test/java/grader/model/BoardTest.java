package grader.model;

import model.Board;
import model.Player;
import model.squares.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    // -------------------------------
    // 1. Board structure
    // -------------------------------
    @Test
    void testBoardSize() {
        assertEquals(20, board.getBoardSize());
        assertEquals(20, board.getSquares().size());
    }

    @Test
    void testNoNullSquares() {
        for (Square square : board.getSquares()) {
            assertNotNull(square);
        }
    }

    // -------------------------------
    // 2. Special squares positions
    // -------------------------------
    @Test
    void testGoSquareAtPosition0() {
        Square square = board.getSquare(0);
        assertTrue(square instanceof GoSquare);
        assertEquals(0, square.getPosition());
    }

    @Test
    void testJailSquareAtPosition5() {
        Square square = board.getSquare(5);
        assertTrue(square instanceof JailSquare);
        assertEquals(5, square.getPosition());
    }

    @Test
    void testLotterySquareAtPosition10() {
        Square square = board.getSquare(10);
        assertTrue(square instanceof LotterySquare);
    }

    @Test
    void testTaxSquareAtPosition4() {
        Square square = board.getSquare(4);
        assertTrue(square instanceof TaxSquare);
    }

    @Test
    void testAirplaneSquareAtPosition15() {
        Square square = board.getSquare(15);
        assertTrue(square instanceof AirplaneSquare);
    }

    @Test
    void testCardSquares() {
        assertTrue(board.getSquare(3) instanceof CardSquare);
        assertTrue(board.getSquare(8) instanceof CardSquare);
        assertTrue(board.getSquare(13) instanceof CardSquare);
    }

    // -------------------------------
    // 3. Property check
    // -------------------------------
    @Test
    void testBangkokPropertyAt19() {
        Square square = board.getSquare(19);
        assertTrue(square instanceof Property);

        Property property = (Property) square;
        assertEquals("BANGKOK", property.getName());
        assertEquals(19, property.getPosition());
    }

    // -------------------------------
    // 4. Behavior test (Jail trap)
    // -------------------------------
    @Test
    void testLandOnJailSendPlayerToJail() {
        Player player = new Player("TestPlayer");

        Square jailSquare = board.getSquare(5);
        jailSquare.landOn(player);

        assertTrue(player.isInJail());
        assertEquals(5, player.getPosition());
        assertEquals(2, player.getJailTurns());
    }

    // -------------------------------
    // 5. getSquare wrap around
    // -------------------------------
    @Test
    void testGetSquareWrapAround() {
        Square square = board.getSquare(25); // 25 % 20 = 5
        assertEquals(5, square.getPosition());
        assertTrue(square instanceof JailSquare);
    }
}
