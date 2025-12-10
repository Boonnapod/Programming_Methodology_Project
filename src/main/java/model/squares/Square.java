package model.squares;

import model.Player;

/**
 * Abstract base class representing a square on the game board.
 * <p>
 * All specific types of squares (Property, Tax, Jail, etc.) must extend this class.
 * It defines the common attributes (name, position) and the contract for interaction via {@link #landOn(Player)}.
 * </p>
 */
public abstract class Square {
    private final String name;
    private final int position;

    /**
     * Constructs a new Square.
     *
     * @param name     The name of the square (e.g., "Go", "Central Park").
     * @param position The index of the square on the board (0 to boardSize-1).
     */
    public Square(String name, int position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Gets the name of the square.
     *
     * @return The square's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the position index of the square on the board.
     *
     * @return The position index.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Defines the action to be taken when a player lands on this square.
     * <p>
     * This method applies polymorphism, allowing different square types to handle player interactions differently
     * (e.g., paying rent, drawing a card, or going to jail).
     * </p>
     *
     * @param player The player who landed on the square.
     */
    public abstract void landOn(Player player);
}