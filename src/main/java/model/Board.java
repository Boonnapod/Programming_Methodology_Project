package model;

import model.squares.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board.
 * <p>
 * The board consists of a fixed number of squares (20), including Properties,
 * Special squares (Go, Jail, Airport), and Action squares (Card, Tax, Lottery).
 * This class handles the initialization and retrieval of these squares.
 * </p>
 */
public class Board {
    private final List<Square> squares;
    private static final int BOARD_SIZE = 20;

    /**
     * Constructs a new Board and initializes all squares.
     */
    public Board() {
        this.squares = new ArrayList<>(BOARD_SIZE);
        initializeBoard();
    }

    /**
     * Sets up the board layout by creating and placing specific Square objects at defined indices.
     */
    private void initializeBoard() {
        // 1. Initialize list with nulls
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares.add(null);
        }

        // 2. Set Special Squares
        squares.set(0, new GoSquare(0)); // 0: Go (Start)

        // ****************************************************************
        // 3. Configure Jail (Index 5) as a Trap
        // Uses an anonymous class to override behavior: landing here immediately sends player to jail.
        // ****************************************************************
        squares.set(5, new JailSquare("Jail", 5) {
            @Override
            public void landOn(Player player) {
                System.out.println(player.getName() + " landed on Jail.");
                System.out.println(">> YOU ARE ARRESTED!");

                // Immediate arrest logic
                player.goToJail();
            }
        });

        // Lottery Square (Index 10)
        squares.set(10, new LotterySquare("Lottery", 10));

        // Card Squares (Chance/Action)
        squares.set(3, new CardSquare("Mystery Card", 3));
        squares.set(8, new CardSquare("Mystery Card", 8));
        squares.set(13, new CardSquare("Mystery Card", 13));

        // Airplane/Travel Square (Index 15)
        squares.set(15, new AirplaneSquare("Airport", 15));

        // Tax Square (Index 4)
        squares.set(4, new TaxSquare("Income Tax", 4));

        // 4. Initialize Properties (Thai Provinces/Cities)
        squares.set(1, new Property("UDONTHANI", 1, 200, 100));
        squares.set(2, new Property("CHAIYAPHUM", 2, 220, 110));
        squares.set(6, new Property("BURIRAM", 6, 180, 90));
        squares.set(7, new Property("CHAI NAT", 7, 260, 130));

        squares.set(9, new Property("KRABI", 9, 240, 120));
        squares.set(11, new Property("LOPBURI", 11, 180, 90));
        squares.set(12, new Property("PATTAYA", 12, 210, 105));
        squares.set(14, new Property("PATTANI", 14, 250, 125));

        squares.set(16, new Property("AYUTTHAYA", 16, 250, 125));
        squares.set(17, new Property("SARABURI", 17, 270, 135));
        squares.set(18, new IslandSquare("PHUKET ISLAND", 18, 280, 140));

        // ****************************************************************
        // 5. Set Bangkok as the final property
        // ****************************************************************
        squares.set(19, new Property("BANGKOK", 19, 300, 150));

        // 6. Fill remaining null slots to prevent errors
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (squares.get(i) == null) {
                squares.set(i, new Property("Square " + i, i, 80, 10));
            }
        }
    }

    /**
     * Retrieves the square at a specific index.
     * <p>
     * The index is wrapped using modulo arithmetic ({@code index % BOARD_SIZE})
     * to prevent IndexOutOfBoundsExceptions and handle circular movement.
     * </p>
     *
     * @param index The board position index.
     * @return The {@link Square} at that position.
     */
    public Square getSquare(int index) {
        return squares.get(index % BOARD_SIZE);
    }

    /**
     * Gets the full list of squares on the board.
     *
     * @return The list of all squares.
     */
    public List<Square> getSquares() {
        return squares;
    }

    /**
     * Gets the total size of the board.
     *
     * @return The number of squares on the board (20).
     */
    public int getBoardSize() {
        return BOARD_SIZE;
    }
}