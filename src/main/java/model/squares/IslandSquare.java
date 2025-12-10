package model.squares;

import model.Player;

public class IslandSquare extends Square implements Rentable {
    private final int price;
    private final int rent;
    private Player owner = null;

    public IslandSquare(String name, int position, int price, int rent) {
        super(name, position);
        this.price = price;
        this.rent = rent;
    }

    @Override
    public void landOn(Player player) {
        // Logic การจ่ายเงิน/ซื้อจะถูกจัดการใน GameBoardScreen (เหมือน Property)
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
    public int getPrice() {
        return price;
    }

    public int getRent() {
        return rent;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}