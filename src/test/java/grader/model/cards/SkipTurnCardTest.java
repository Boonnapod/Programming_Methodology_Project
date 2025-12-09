package grader.model.cards;

import model.Board;
import model.Player;
import model.cards.SkipTurnCard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkipTurnCardTest {

    @Test
    void testExecute_AccumulatesSkipTurns() {
        // 1. Setup
        Player player = new Player("Sleepy Head");
        SkipTurnCard card = new SkipTurnCard("Sleep tight", 2); // การ์ดสั่งหยุด 2 ตา

        // Board ไม่ได้ใช้ใน Logic นี้ ใส่ null ได้เลย
        Board board = null;

        // 2. Action 1: โดนครั้งแรก
        card.execute(player, board);
        assertEquals(2, player.getSkipTurns(), "Should set skip turns to 2");

        // 3. Action 2: โดนซ้ำอีกรอบ (เพื่อเช็คว่ามันบวกเพิ่ม ไม่ใช่ทับค่าเดิม)
        card.execute(player, board);
        assertEquals(4, player.getSkipTurns(), "Should accumulate skip turns (2 + 2 = 4)");
    }
}