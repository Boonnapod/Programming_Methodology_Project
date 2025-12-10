package model.squares;

/**
 * Interface for squares that can charge rent (e.g., Property, Utilities).
 */
public interface Rentable {
    /**
     * Calculates the rent amount a player must pay.
     *
     * @param diceRoll The total value of the dice roll (used for Utility rent calculation logic).
     * @return The calculated rent amount.
     */
    int calculateRent(int diceRoll);
}