package model.squares;

import model.Player;

/**
 * Represents the Jail square.
 * <p>
 * This square serves as the location for players who are in jail (visiting or incarcerated).
 * The logic for sending a player to jail is typically handled by "Go to Jail" squares or cards.
 * </p>
 */
public class JailSquare extends Square {
    public JailSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        // This is just the location. Jail status is managed by the Player state.
        System.out.println(player.getName() + " is in Jail.");
    }
}