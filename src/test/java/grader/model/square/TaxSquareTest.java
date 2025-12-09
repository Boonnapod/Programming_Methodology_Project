package grader.model.square;

import model.Player;
import model.squares.TaxSquare;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaxSquareTest {

    @Test
    void testLandOn_DeductsTaxInRange() {
        // 1. Setup
        TaxSquare taxSquare = new TaxSquare("Revenue Dept", 4);
        Player player = new Player("Citizen");

        // เติมเงินให้ Player ก่อน เดี๋ยวไม่มีจ่าย (สมมติเริ่มที่ 1000)
        player.addMoney(1000);
        int initialMoney = player.getMoney();

        // 2. Action
        taxSquare.landOn(player);
        int taxPaid = taxSquare.getLastTaxPaid();

        // 3. Assert Logic

        // เช็คช่วงของภาษี (100, 150, 200, 250, 300)
        assertTrue(taxPaid >= 100 && taxPaid <= 300, "Tax should be between 100 and 300");
        assertEquals(0, taxPaid % 50, "Tax should be a multiple of 50");

        // เช็คว่าเงินลดลงจริงไหม
        assertEquals(initialMoney - taxPaid, player.getMoney(), "Player money should decrease by tax amount");
    }
}