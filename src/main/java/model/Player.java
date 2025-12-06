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
    }

    public boolean deductMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        // ไม่ต้องจัดการล้มละลายที่นี่ GameController จะจัดการเอง
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
     * การเคลื่อนที่แบบกระโดด/วาร์ป: ไม่เก็บเงินผ่าน Go
     */
    public void warp(int newPosition) {
        // ไม่ต้องตรวจสอบการผ่าน Go
        this.position = newPosition % SMALL_BOARD_SIZE;
    }

    public void goToJail() {
        this.inJail = true;
        this.position = 5; // ตำแหน่ง Jail ในกระดาน 20 ช่อง
    }

    public void releaseFromJail() {
        this.inJail = false;
    }
}