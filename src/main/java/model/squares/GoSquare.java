package model.squares;

import model.Player;

/**
 * Represents the "Go" (Start) square.
 * <p>
 * Players typically collect a salary when passing or landing on this square.
 * The salary logic is handled within {@link Player#move(int)}.
 * </p>
 */
public class GoSquare extends Square {
    public GoSquare(int position) {
        super("Go", position);
    }

    @Override
    public void landOn(Player player) {
        System.out.println(player.getName() + " landed on Go.");
    }
}