package model.squares;

import model.Player;

/**
 * Represents an Airplane/Travel square.
 * <p>
 * This square allows players to "fly" to any other square on the board.
 * The destination selection logic is handled by the {@code GameController}.
 * </p>
 */
public class AirplaneSquare extends Square {

    /**
     * Constructs an AirplaneSquare with a specific name and position.
     * * @param name     The name of the square.
     * @param position The position index on the board.
     */
    public AirplaneSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        // Logic for choosing a destination is handled in GameController
        System.out.println(player.getName() + " landed on " + getName() + ". Choose your destination!");
    }
}