package model;

public class Player {
    private final String name;
    private int money;
    private int position; // 0 to 19
    private boolean inJail;
    private int skipTurns; // <--- NEW: นับจำนวนตาที่ต้องหยุดเดิน
    private static final int SMALL_BOARD_SIZE = 20;

    public Player(String name, int initialMoney) {
        this.name = name;
        this.money = initialMoney;
        this.position = 0;
        this.inJail = false;
        this.skipTurns = 0; // เริ่มต้นไม่ต้องหยุดเดิน
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isInJail() { return inJail; }
    public int getSkipTurns() { return skipTurns; } // <--- NEW
    public static int getInitialMoney() { return 1500; } // <--- NEW: สำหรับคำนวณการ์ด

    // --- Setters ---
    public void setSkipTurns(int skipTurns) { this.skipTurns = skipTurns; } // <--- NEW

    // --- Actions ---
    public void addMoney(int amount) {
        this.money += amount;
        System.out.println("💵 " + this.name + " received $" + amount + ". New Balance: $" + this.money); // <--- เพิ่ม LOG
    }

    public boolean deductMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            System.out.println("💸 " + this.name + " paid $" + amount + ". New Balance: $" + this.money); // <--- เพิ่ม LOG
            return true;
        }
        // เงินไม่พอ (Logic ล้มละลายอยู่ใน GameController)
        else{
            this.money -= amount;
            return false;
        }
    }

    public void goToJail() {
        this.inJail = true; // <--- ต้องแน่ใจว่าบรรทัดนี้ทำงาน
        this.position = 5; // ตำแหน่งคุก (Position 5)
        System.out.println(name + " is going to Jail!");
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
     * การเคลื่อนที่แบบกระโดด/วาร์ป: ไม่เก็บเงินผ่าน Go
     */
    public void warp(int newPosition) {
        // ไม่ต้องตรวจสอบการผ่าน Go
        this.position = newPosition % SMALL_BOARD_SIZE;
    }

    public void releaseFromJail() {
        this.inJail = false;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }
}