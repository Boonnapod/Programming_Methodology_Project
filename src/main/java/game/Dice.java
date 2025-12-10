package game;

import java.util.Random;

/**
 * Represents the dice used in the game.
 * <p>
 * Supports standard random rolling and a forced result feature for testing purposes.
 * </p>
 */
public class Dice {
    private final Random random;
    private int forcedResult = 0;

    /**
     * Constructs a new Dice with a random number generator.
     */
    public Dice() {
        this.random = new Random();
    }

    /**
     * Sets a forced result for the next roll.
     * <p>
     * This method is intended for <b>testing purposes only</b>.
     * The forced value will be used exactly once for the next {@link #roll()} call,
     * after which the dice reverts to normal random behavior.
     * </p>
     *
     * @param result The specific number (1-6) the dice should return on the next roll.
     */
    public void setForcedResult(int result) {
        this.forcedResult = result;
    }

    /**
     * Rolls the dice to generate a number between 1 and 6.
     * <p>
     * If a forced result has been set via {@link #setForcedResult(int)}, that value is returned
     * and the forced state is cleared. Otherwise, a random number is generated.
     * </p>
     *
     * @return An integer between 1 and 6.
     */
    public int roll() {
        if (forcedResult != 0) {
            int result = forcedResult;
            forcedResult = 0; // Reset immediately after use
            return result;
        }

        return random.nextInt(6) + 1;
    }
}