package model;

public class Player {
    private final String name;
    private int money;
    private int position; // 0 to 19
    private boolean inJail;
    private int skipTurns;
    private static final int SMALL_BOARD_SIZE = 20;
    private static final int INITIAL_MONEY = 1500;

    public Player(String name, int initialMoney) {
        this.name = name;
        this.money = initialMoney;
        this.position = 0;
        this.inJail = false;
        this.skipTurns = 0;
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isInJail() { return inJail; }
    public int getSkipTurns() { return skipTurns; }
    public static int getInitialMoney() { return INITIAL_MONEY; }

    // --- Setters ---
    public void setSkipTurns(int skipTurns) { this.skipTurns = skipTurns; }
    public void setInJail(boolean inJail) { this.inJail = inJail; }

    // --- Actions ---
    public void addMoney(int amount) {
        this.money += amount;
        System.out.println("💵 " + this.name + " received $" + amount + ". New Balance: $" + this.money);
    }

    public boolean deductMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            System.out.println("💸 " + this.name + " paid $" + amount + ". New Balance: $" + this.money);
            return true;
        }
        // เงินไม่พอ (Logic ล้มละลายอยู่ใน GameBoardScreen)
        return false;
    }

    public void goToJail() {
        this.inJail = true;
        this.position = 5; // ตำแหน่งคุก (Position 5)
        System.out.println(name + " is going to Jail!");
    }

    /**
     * ออกจากคุกโดยการจ่ายเงิน (Logic การจ่ายเงินอยู่ใน GUI)
     */
    public boolean releaseFromJail() {
        if (this.inJail) {
            this.inJail = false;
            System.out.println(name + " is released from Jail.");
            return true;
        }
        return false;
    }

    /**
     * การเคลื่อนที่ปกติ: ตรวจสอบการผ่าน Go (เก็บเงิน)
     */
    public void move(int steps) {
        int oldPosition = this.position;
        this.position = (oldPosition + steps) % SMALL_BOARD_SIZE;

        // ตรวจสอบการผ่าน Go
        if (oldPosition + steps >= SMALL_BOARD_SIZE) {
            addMoney(200);
        }
    }

    /**
     * การเคลื่อนที่แบบกระโดด/วาร์ป: ไม่ตรวจสอบการผ่าน Go
     */
    public void warp(int newPosition) {
        this.position = newPosition;
    }
}