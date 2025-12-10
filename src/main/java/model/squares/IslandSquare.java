package model.squares;

import model.Player;

/**
 * Represents an Island square, functioning similarly to a Property.
 * <p>
 * This class demonstrates how different types of properties can be implemented using the {@link Rentable} interface.
 * </p>
 */
public class IslandSquare extends Square implements Rentable {
    private final int price;
    private final int rent;
    private Player owner = null;

    /**
     * Constructs an IslandSquare.
     *
     * @param name     The name of the island.
     * @param position The position on the board.
     * @param price    The purchase price.
     * @param rent     The fixed rent cost.
     */
    public IslandSquare(String name, int position, int price, int rent) {
        super(name, position);
        this.price = price;
        this.rent = rent;
    }

    @Override
    public void landOn(Player player) {
        if (owner != null && owner != player) {
            System.out.println("🏝️ " + player.getName() + " landed on " + getName() + " owned by " + owner.getName());
        } else if (owner == player) {
            System.out.println("🏝️ " + player.getName() + " landed on their own island: " + getName());
        } else {
            System.out.println("🏝️ " + player.getName() + " landed on unowned island: " + getName() + " (Price: $" + price + ")");
        }
    }

    @Override
    public int calculateRent(int diceRoll) {
        return rent;
    }

    // --- Getters and Setters ---
    public int getPrice() { return price; }
    public int getRent() { return rent; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
}