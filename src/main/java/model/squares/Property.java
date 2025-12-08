package model.squares;

import model.Player;

public class Property extends Square implements Rentable {
    private final int price;
    private final int baseRent;
    private final int upgradeCost; // <--- NEW: ราคาอัพเกรดเป็น Landmark
    private Player owner = null;
    private boolean isLandmark = false; // <--- NEW: สถานะ Landmark

    public Property(String name, int position, int price, int baseRent) {
        super(name, position);
        this.price = price;
        this.baseRent = baseRent;
        // กำหนดราคาอัพเกรดให้ง่ายขึ้น อาจเป็น 2 เท่าของราคาซื้อ
        this.upgradeCost = price * 2;
    }

    @Override
    public void landOn(Player player) {
        if (owner != null && owner != player) {
            int rent = calculateRent(0);
            System.out.println("Owned by " + owner.getName() + ". Rent: $" + rent + " paid by " + player.getName());

        } else if (owner == player) {
            // เมื่อผู้เล่นที่เป็นเจ้าของมาตกช่องตัวเอง Logic การอัพเกรดจะเกิดขึ้นใน GameController
            System.out.println("You own this property. Landed on " + (isLandmark ? "Landmark!" : "your property."));
        } else {
            System.out.println("Property is unowned. Price: $" + price);
        }
    }

    @Override
    public int calculateRent(int diceRoll) {
        int rent = baseRent;
        if (isLandmark) {
            // NEW: ค่าเช่าเพิ่มขึ้น 3 เท่าเมื่อเป็น Landmark
            rent *= 3;
        }
        return rent;
    }

    // --- Getters and Setters ---
    public int getPrice() { return price; }
    public int getUpgradeCost() { return upgradeCost; } // <--- NEW
    public Player getOwner() { return owner; }
    public boolean isLandmark() { return isLandmark; } // <--- NEW

    public void setOwner(Player owner) { this.owner = owner; }
    public void setLandmark(boolean isLandmark) { this.isLandmark = isLandmark; } // <--- NEW
}