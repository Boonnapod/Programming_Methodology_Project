package model.squares;

import model.Player;

/**
 * Represents a purchasable property square on the board.
 * <p>
 * Players can buy properties, pay rent to owners, and upgrade them to Landmarks
 * to increase the rent value.
 * </p>
 */
public class Property extends Square implements Rentable {
    private final int price;
    private final int baseRent;
    private final int upgradeCost;
    private Player owner = null;
    private boolean isLandmark = false;

    /**
     * Constructs a Property square.
     *
     * @param name     The name of the property.
     * @param position The position on the board.
     * @param price    The purchase price of the property.
     * @param baseRent The initial rent value.
     */
    public Property(String name, int position, int price, int baseRent) {
        super(name, position);
        this.price = price;
        this.baseRent = baseRent;
        // Upgrade cost is set to double the purchase price for simplicity
        this.upgradeCost = price * 2;
    }

    /**
     * Handles the event when a player lands on this property.
     * Displays status messages about ownership and rent.
     *
     * @param player The player landing on the square.
     */
    @Override
    public void landOn(Player player) {
        if (owner != null && owner != player) {
            int rent = calculateRent(0);
            System.out.println("Owned by " + owner.getName() + ". Rent: $" + rent + " paid by " + player.getName());

        } else if (owner == player) {
            // Upgrade logic is handled by the GameController
            System.out.println("You own this property. Landed on " + (isLandmark ? "Landmark!" : "your property."));
        } else {
            System.out.println("Property is unowned. Price: $" + price);
        }
    }

    /**
     * Calculates the current rent of the property.
     * <p>
     * If the property is upgraded to a Landmark, the rent is tripled.
     * </p>
     *
     * @param diceRoll The dice roll value (unused for standard properties).
     * @return The calculated rent amount.
     */
    @Override
    public int calculateRent(int diceRoll) {
        int rent = baseRent;
        if (isLandmark) {
            rent *= 3; // Rent triples for landmarks
        }
        return rent;
    }

    // --- Getters and Setters ---
    public int getPrice() { return price; }
    public int getUpgradeCost() { return upgradeCost; }
    public Player getOwner() { return owner; }
    public boolean isLandmark() { return isLandmark; }

    public void setOwner(Player owner) { this.owner = owner; }
    public void setLandmark(boolean isLandmark) { this.isLandmark = isLandmark; }
}