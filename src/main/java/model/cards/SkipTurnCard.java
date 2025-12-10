package model.cards;

import model.Board;
import model.Player;

/**
 * A card that penalizes the player by forcing them to skip future turns.
 */
public class SkipTurnCard extends Card {
    private final int turnsToSkip;

    /**
     * Constructs a SkipTurnCard.
     *
     * @param description The text describing why the player is skipping a turn.
     * @param turnsToSkip The number of turns the player must miss.
     */
    public SkipTurnCard(String description, int turnsToSkip) {
        super(description);
        this.turnsToSkip = turnsToSkip;
    }

    /**
     * Increases the player's skip turn counter.
     * <p>
     * The logic accumulates skipped turns ({@code +=}), so if a player is already skipping turns
     * and draws this card again, the new penalty is added to the existing one.
     * </p>
     *
     * @param player The player who drew the card.
     * @param board  The game board (unused in this specific logic).
     */
    @Override
    public void execute(Player player, Board board) {
        System.out.println(">> Card Action: " + getDescription());
        player.setSkipTurns(player.getSkipTurns() + turnsToSkip);
        System.out.println(player.getName() + " must skip " + player.getSkipTurns() + " turn(s).");
    }
}