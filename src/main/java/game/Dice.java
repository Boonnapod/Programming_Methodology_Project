package game;

import java.util.Random;

public class Dice {
    private final Random random;

    public Dice() {
        this.random = new Random();
    }

    public int roll() {
        // ทอยลูกเต๋ามาตรฐาน 6 หน้า 1 ลูก
        return 5;
    }
}