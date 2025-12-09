package grader.model.cards;

import model.Board;
import model.Player;
import model.cards.ReceiveMoneyCard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReceiveMoneyCardTest {

    @Test
    void testExecute_AddsCorrectAmount() {
        // 1. Setup
        int prizeAmount = 500;
        ReceiveMoneyCard card = new ReceiveMoneyCard("Win Lottery", prizeAmount);

        Player player = new Player("Lucky Player");
        int initialMoney = player.getMoney();

        // Board ไม่ได้ถูกใช้ใน Logic ของการ์ดใบนี้ แต่ต้องส่งตาม Method Signature
        // (ใส่ null ก็ได้ถ้ามั่นใจว่า code ไม่เรียกใช้ board, แต่ใส่ object จริงปลอดภัยกว่า)
        Board board = new Board();

        // 2. Action
        card.execute(player, board);

        // 3. Assert: เช็คว่าเงินเพิ่มขึ้นตรงตามจำนวนที่ตั้งไว้ไหม
        assertEquals(initialMoney + prizeAmount, player.getMoney(),
                "Player money should increase exactly by the card amount");
    }
}