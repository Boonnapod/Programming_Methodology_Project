package grader.model.square;

import model.Player;
import model.squares.LotterySquare;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LotterySquareTest {

    @Test
    void testLandOn_WinsMoneyInRange() {
        // 1. Setup
        LotterySquare lottery = new LotterySquare("Lotto Point", 5);
        Player player = new Player("Lucky Guy");
        int initialMoney = player.getMoney();

        // 2. Action (ลองเรียกหลายๆ รอบก็ได้ถ้าอยากมั่นใจ แต่รอบเดียวก็พอไหว)
        lottery.landOn(player);
        int prize = lottery.getLastPrize();

        // 3. Assert Logic

        // เช็คว่ารางวัลอยู่ในช่วง 100 - 500
        assertTrue(prize >= 100 && prize <= 500, "Prize should be between 100 and 500");

        // เช็คว่าเป็นตัวเลขที่หาร 100 ลงตัว (100, 200, 300...)
        assertEquals(0, prize % 100, "Prize should be multiple of 100");

        // เช็คว่าเงินเข้ากระเป๋าผู้เล่นจริง
        assertEquals(initialMoney + prize, player.getMoney(), "Player money should increase by prize amount");
    }
}
