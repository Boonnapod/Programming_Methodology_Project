package game;

import java.util.Random;

public class Dice {
    private final Random random;

    // NEW: Field สำหรับเก็บค่าที่ต้องการบังคับให้ roll() คืนค่า
    private int forcedResult = 0;

    public Dice() {
        this.random = new Random();
    }

    /**
     * สำหรับการทดสอบ: ตั้งค่าตัวเลขที่ต้องการให้เมธอด roll() คืนค่าในครั้งถัดไป
     * @param result ตัวเลขที่ต้องการให้ roll() คืนค่า (1-6)
     */
    public void setForcedResult(int result) {
        this.forcedResult = result;
    }

    public int roll() {
        // NEW: ตรวจสอบว่ามีการบังคับผลลัพธ์ไว้หรือไม่
        if (forcedResult != 0) {
            int result = forcedResult;
            forcedResult = 0; // รีเซ็ตทันทีหลังการใช้ 1 ครั้ง
            return result;
        }

        // โค้ดเดิม: ทอยลูกเต๋ามาตรฐาน
        return random.nextInt(6) + 1;
    }
}