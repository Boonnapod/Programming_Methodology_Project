package grader.model.cards;

import model.Board;
import model.Player;
import model.cards.MoveToAirPlane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveToAirPlaneTest {

    private MoveToAirPlane card;
    private Board board;
    private Player player;

    @BeforeEach
    void setUp() {
        card = new MoveToAirPlane("Go to Airport");
        board = new Board(); // มั่นใจว่า Board มีการสร้าง Square ครบ 20 ช่องใน Constructor
        player = new Player("Traveler");
    }

    @Test
    void testExecute_MoveForward() {
        // กรณี 1: อยู่ช่อง 5 ไปช่อง 15 (เดินหน้าปกติ)
        player.setPosition(5);

        card.execute(player, board);

        assertEquals(15, player.getPosition(), "Player should land on Airport (Index 15)");
    }

    @Test
    void testExecute_MoveWrapAround() {
        // กรณี 2: อยู่ช่อง 18 ไปช่อง 15 (ต้องเดินผ่านจุด Start)
        player.setPosition(18); // อยู่เกือบท้ายกระดาน

        card.execute(player, board);

        assertEquals(15, player.getPosition(), "Player should land on Airport (Index 15) even after wrapping around");

        // *หมายเหตุ: ถ้า Player.move() มี logic แจกเงินเมื่อผ่านจุด Start
        // คุณอาจจะ assert เงินเพิ่มด้วยก็ได้ แต่แค่เช็ค Position ก็เพียงพอสำหรับ Card นี้ครับ
    }
}
