package grader.game;

import game.Dice;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @Test
    void testRoll_ReturnsValidRange() {
        Dice dice = new Dice();

        // ลองทอย 100 ครั้ง เพื่อความชัวร์ว่าเลขไม่ออกนอกลู่นอกทาง
        for (int i = 0; i < 100; i++) {
            int result = dice.roll();
            assertTrue(result >= 1 && result <= 6,
                    "Dice result must be between 1 and 6. Got: " + result);
        }
    }

    @Test
    void testForcedResult_Works() {
        Dice dice = new Dice();

        // สั่งล็อคเลข 5
        dice.setForcedResult(5);

        // ทอยครั้งแรกต้องได้ 5
        assertEquals(5, dice.roll(), "Dice should return the forced value");
    }

    @Test
    void testForcedResult_ResetsAfterOneRoll() {
        Dice dice = new Dice();

        // สั่งล็อคเลข 3
        dice.setForcedResult(3);

        // ครั้งแรกได้ 3
        assertEquals(3, dice.roll());

        // ครั้งที่สองต้องกลับมาเป็นสุ่ม (1-6)
        // หมายเหตุ: มีโอกาสสุ่มได้ 3 ซ้ำ แต่สิ่งสำคัญคือโค้ดต้องไม่ Error
        int nextResult = dice.roll();
        assertTrue(nextResult >= 1 && nextResult <= 6, "Dice should return to normal random mode");
    }
}