package model.squares;

import model.Player;

/**
 * Represents a Card square (Chance/Community Chest).
 * <p>
 * Landing here triggers the drawing of a card from the deck.
 * The card drawing logic is managed by {@code GameController} and {@code CardManager}.
 * </p>
 */
public class CardSquare extends Square {

    public CardSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        System.out.println(player.getName() + " landed on " + getName() + ". Prepare to draw a card!");
    }
}