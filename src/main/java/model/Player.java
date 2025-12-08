package model;

public class Player {
    private final String name;
    private int money;
    private int position; // 0 to 19
    private boolean inJail;
    private int skipTurns;

    // *** เพิ่มตัวแปรนับถอยหลังการติดคุก ***
    private int jailTurns = 0;

    private static final int SMALL_BOARD_SIZE = 20;
    private static final int INITIAL_MONEY_DEFAULT = 2000;

    public Player(String name) {
        this.name = name;
        this.money = INITIAL_MONEY_DEFAULT;
        this.position = 0;
        this.inJail = false;
        this.skipTurns = 0;
        this.jailTurns = 0;
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isInJail() { return inJail; }
    public int getSkipTurns() { return skipTurns; }
    public static int getInitialMoney() { return INITIAL_MONEY_DEFAULT; }

    // *** Getter สำหรับจำนวนรอบที่เหลือในคุก ***
    public int getJailTurns() { return jailTurns; }

    // --- Setters / Actions ---
    public void setSkipTurns(int skipTurns) { this.skipTurns = skipTurns; }

    public void addMoney(int amount) {
        this.money += amount;
        System.out.println("💵 " + this.name + " received $" + amount + ". New Balance: $" + this.money);
    }

    public boolean deductMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            System.out.println("💸 " + this.name + " paid $" + amount + ". New Balance: $" + this.money);
            return true;
        } else {
            // กรณีเงินไม่พอ (logic ล้มละลาย)
            this.money -= amount;
            return false;
        }
    }

    /**
     * สั่งให้ผู้เล่นติดคุก
     * ย้ายไปช่อง 5 และตั้งค่าโทษ 2 ตา
     */
    public void goToJail() {
        this.inJail = true;
        this.position = 5; // ย้ายไป Jail (ตำแหน่ง 5)
        this.jailTurns = 2; // *** กำหนดให้ติด 2 ตา ***
        System.out.println("🚨 " + name + " is sent to Jail! (Stuck for 2 turns)");
    }

    /**
     * ปล่อยตัวจากคุก
     */
    public void releaseFromJail() {
        this.inJail = false;
        this.jailTurns = 0;
        System.out.println("🔓 " + name + " is released from Jail.");
    }

    /**
     * ลดจำนวนรอบที่ต้องติดคุก (ใช้ตอนเลือก Wait)
     */
    public void decrementJailTurns() {
        if (this.jailTurns > 0) {
            this.jailTurns--;
        }
    }

    public void move(int steps) {
        int oldPosition = this.position;
        this.position = (oldPosition + steps) % SMALL_BOARD_SIZE;

        // ตรวจสอบการผ่าน Go (ช่อง 0)
        if (oldPosition + steps >= SMALL_BOARD_SIZE) {
            addMoney(200);
            System.out.println("🎉 " + name + " passed Go and collected $200.");
        }
    }

    // สำหรับการวาร์ป (เช่น นั่งเครื่องบิน) โดยไม่ผ่าน Go Logic ปกติ
    public void warp(int position) {
        this.position = position;
    }
}