package model.cards;

import model.Board;
import model.Player;

/**
 * A card that awards a specific amount of money to the player.
 * Used for lottery wins, tax refunds, or other bonuses.
 */
public class ReceiveMoneyCard extends Card {
    private final int amount;

    /**
     * Constructs a ReceiveMoneyCard.
     *
     * @param description The text describing the source of the money.
     * @param amount      The amount of money to add to the player's balance.
     */
    public ReceiveMoneyCard(String description, int amount) {
        super(description);
        this.amount = amount;
    }

    /**
     * Adds the specified amount of money to the player's balance.
     *
     * @param player The player who drew the card.
     * @param board  The game board (unused in this specific logic).
     */
    @Override
    public void execute(Player player, Board board) {
        System.out.println(">> Card Action: " + getDescription());
        player.addMoney(amount);
        System.out.println(player.getName() + " collected $" + amount);
    }
}