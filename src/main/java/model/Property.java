package model;

public class Property extends Square implements Rentable {
    private final int price;
    private final int baseRent;
    private Player owner = null;

    public Property(String name, int position, int price, int baseRent) {
        super(name, position);
        this.price = price;
        this.baseRent = baseRent;
    }

    @Override
    public void landOn(Player player) {
        if (owner != null && owner != player) {
            int rent = calculateRent(0); // สมมติว่าไม่ใช้ค่าลูกเต๋าในการคำนวณ
            System.out.println("Owned by " + owner.getName() + ". Rent: $" + rent + " paid by " + player.getName());

            // โลจิกการจ่าย/รับค่าเช่า
            if (player.deductMoney(rent)) {
                owner.addMoney(rent);
            }
            // หาก player จ่ายไม่ได้ โลจิกการล้มละลายจะถูกจัดการใน GameController
        } else if (owner == null) {
            System.out.println("Property is unowned. Price: $" + price);
        } else {
            System.out.println("You own this property.");
        }
    }

    @Override
    public int calculateRent(int diceRoll) {
        // สามารถเพิ่ม logic การคำนวณค่าเช่าที่ซับซ้อนขึ้นได้
        return baseRent;
    }

    // --- Getters and Setters ---
    public int getPrice() { return price; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
}