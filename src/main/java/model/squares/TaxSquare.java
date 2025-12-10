package model.squares;

import model.Player;
import java.util.Random;

/**
 * Represents a Tax square where players must pay a randomized tax amount.
 */
public class TaxSquare extends Square {
    private final Random random;
    private int lastTaxPaid = 0;

    /**
     * Constructs a TaxSquare.
     *
     * @param name     The name of the square (e.g., "Tax Office").
     * @param position The position on the board.
     */
    public TaxSquare(String name, int position) {
        super(name, position);
        this.random = new Random();
    }

    /**
     * Calculates a random tax amount and deducts it from the player's money.
     * The tax range is typically between $100 and $300 (multiples of 50).
     *
     * @param player The player landing on the square.
     */
    @Override
    public void landOn(Player player) {
        // Random tax calculation: (0-4 + 2) * 50 = 100 to 300
        int randomTax = (random.nextInt(5) + 2) * 50;

        this.lastTaxPaid = randomTax;

        System.out.println(player.getName() + " landed on " + getName());
        System.out.println(">> TAX PAYABLE: $" + lastTaxPaid);

        player.deductMoney(lastTaxPaid);
    }

    /**
     * Gets the last tax amount calculated.
     * Useful for UI display purposes.
     *
     * @return The most recent tax amount paid.
     */
    public int getLastTaxPaid() {
        return lastTaxPaid;
    }
}