package model.cards;

import model.Board;
import model.Player;

/**
 * Abstract base class representing a generic card in the game.
 * <p>
 * All specific card types (e.g., ReceiveMoney, SkipTurn) must extend this class
 * and implement the {@link #execute(Player, Board)} method to define their unique behavior.
 * </p>
 */
public abstract class Card {
    private final String description;

    /**
     * Constructs a new Card with a description.
     *
     * @param description The text describing the card's effect (e.g., "You won the lottery!").
     */
    public Card(String description) {
        this.description = description;
    }

    /**
     * Gets the description of the card.
     *
     * @return The description string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Executes the specific action associated with this card.
     * <p>
     * This method must be implemented by subclasses to define what happens when
     * a player draws this card (e.g., moving the player, modifying money, or skipping turns).
     * </p>
     *
     * @param player The player who drew the card.
     * @param board  The game board (used for reference if the card action involves board positions).
     */
    public abstract void execute(Player player, Board board);
}