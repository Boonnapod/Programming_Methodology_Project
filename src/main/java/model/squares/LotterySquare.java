package model.squares;

import model.Player;
import java.util.Random;

/**
 * Represents a Lottery square where players have a chance to win money.
 */
public class LotterySquare extends Square {
    private final Random random;
    private int lastPrize = 0;

    /**
     * Constructs a LotterySquare.
     *
     * @param name     The name of the square.
     * @param position The position on the board.
     */
    public LotterySquare(String name, int position) {
        super(name, position);
        this.random = new Random();
    }

    /**
     * Generates a random prize amount and adds it to the player's balance.
     * The prize is typically a multiple of 100 (e.g., $100 - $500).
     *
     * @param player The player landing on the square.
     */
    @Override
    public void landOn(Player player) {
        // Random prize: 1-5 * 100
        int luckyNumber = random.nextInt(5) + 1;
        this.lastPrize = luckyNumber * 100;

        System.out.println(player.getName() + " landed on " + getName());
        System.out.println(">> LOTTERY RESULT: You win $" + lastPrize);

        player.addMoney(lastPrize);
    }

    /**
     * Gets the last prize amount won.
     *
     * @return The most recent prize amount.
     */
    public int getLastPrize() {
        return lastPrize;
    }
}