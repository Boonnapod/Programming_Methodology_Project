package grader.game;

import game.GameController;
import model.Player;
import model.squares.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;
    private final InputStream originalSystemIn = System.in; // เก็บค่า Input เดิมไว้คืนค่าทีหลัง

    @BeforeEach
    void setUp() {
        // สร้าง Controller แบบปกติสำหรับเทสทั่วไป
        controller = new GameController();
    }

    @AfterEach
    void tearDown() {
        // คืนค่า System.in ให้กลับเป็นปกติหลังเทสเสร็จ
        System.setIn(originalSystemIn);
    }

    //Helper method เพื่อจำลองการพิมพ์ของผู้เล่น
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    // ==========================================
    // PART 1: LOGIC TESTS (ไม่ต้องใช้ Scanner)
    // ==========================================

    @Test
    void testMoveToNextPlayer() {
        // เริ่มต้นต้องเป็น Player 1
        assertEquals("Player 1", controller.getCurrentPlayer().getName());

        // เปลี่ยนเทิร์น -> Player 2
        controller.moveToNextPlayer();
        assertEquals("Player 2", controller.getCurrentPlayer().getName());

        // เปลี่ยนเทิร์นอีกรอบ -> วนกลับมา Player 1
        controller.moveToNextPlayer();
        assertEquals("Player 1", controller.getCurrentPlayer().getName());
    }

    @Test
    void testAirplaneMovement_Forward() {
        Player player = controller.getCurrentPlayer();
        player.setPosition(0); // เริ่มที่จุด Start

        // สั่งให้บินไปช่องที่ 5
        controller.handleAirplaneAction(player, 5);

        // ตรวจสอบตำแหน่ง
        assertEquals(5, player.getPosition());
    }

    @Test
    void testAirplaneMovement_WrapAround() {
        Player player = controller.getCurrentPlayer();
        player.setPosition(18); // อยู่ท้ายกระดาน (สมมติกระดานมี 20 ช่อง)

        // สั่งให้บินไปช่องที่ 2 (ต้องเดินผ่านจุด Start)
        controller.handleAirplaneAction(player, 2);

        assertEquals(2, player.getPosition());
        // ตรวจสอบว่าเงินเพิ่มขึ้นจากการผ่านจุด Start (Logic move() ของ Player จัดการเรื่องนี้)
        // หมายเหตุ: ต้องมั่นใจว่า Player.move() มี logic เพิ่มเงินเมื่อวนรอบ
    }

    @Test
    void testHandleBankruptcy() {
        Player victim = controller.getPlayers().get(0);

        // 1. ดึง Property จริงๆ จากกระดานมาสักอัน (แทนการ new ใหม่)
        Property propOnBoard = (Property) controller.getBoard().getSquares().stream()
                .filter(s -> s instanceof Property) // หาช่องที่เป็น Property
                .findFirst()                        // เอาอันแรกที่เจอ
                .orElseThrow(() -> new RuntimeException("No Property found on board"));

        // 2. เซ็ตให้ผู้เล่นที่จะล้มละลายเป็นเจ้าของช่องนั้น
        propOnBoard.setOwner(victim);
        propOnBoard.setLandmark(true);

        // 3. จำลองสถานการณ์ล้มละลาย
        controller.handleBankruptcy(victim);

        // 4. ตรวจสอบผลลัพธ์จากช่องเดิมบนกระดาน
        assertNull(propOnBoard.getOwner(), "Property owner should be null after bankruptcy");
        assertFalse(propOnBoard.isLandmark(), "Landmark status should be removed");
        assertFalse(controller.getPlayers().contains(victim), "Bankrupt player should be removed from game");
    }

    @Test
    void testBuyProperty_Success() {
        // 1. จำลอง Input: พิมพ์ "y" แล้ว Enter
        provideInput("y\n");

        // 2. ต้องสร้าง Controller ใหม่ *หลังจาก* setIn เพื่อให้ Scanner รับค่าจาก Input จำลองของเรา
        GameController interactiveController = new GameController();
        Player buyer = interactiveController.getCurrentPlayer();

        // Setup ที่ดิน
        Property property = new Property("Beach House", 500, 50, 2); // ราคา 500
        buyer.addMoney(1000); // ให้เงินเยอะๆ

        // 3. เรียกเมธอดที่ต้องการเทส
        interactiveController.handlePropertySquare(buyer, property);

        // 4. ตรวจสอบผล
        assertEquals(buyer, property.getOwner(), "Player should own the property after buying");
        // เงินต้องลดลง 500 (สมมติเริ่มเกมมี 2000 + 1000 = 3000 -> เหลือ 2500)
        // หรือเช็คแค่ว่าเงินลดลงตามราคา
        // assertEquals(expectedMoney, buyer.getMoney());
    }

    @Test
    void testBuyProperty_NotEnoughMoney() {
        provideInput("y\n");
        GameController interactiveController = new GameController();
        Player poorPlayer = interactiveController.getCurrentPlayer();

        // --- แก้ไขจุดนี้ ---
        // 1. ล้างเงินเก่าออกให้หมด (ใช้ deductMoney แทน addMoney ติดลบ)
        if (poorPlayer.getMoney() > 0) {
            poorPlayer.deductMoney(poorPlayer.getMoney());
        }

        // 2. ให้เงินไปนิดเดียว (แค่ 10 บาท)
        poorPlayer.addMoney(10);

        // 3. สร้าง Property ที่แพงกว่าเงินที่มีแน่นอน (ราคา 50,000)
        // **เช็ค Constructor Class Property ดีๆ ว่าช่องไหนคือ Price**
        // สมมติ: new Property(name, price, rent, ...)
        Property expensiveProp = new Property("Luxury Hotel", 50000, 100, 2);

        // --- รัน Test ---
        interactiveController.handlePropertySquare(poorPlayer, expensiveProp);

        // ผลลัพธ์ต้องเป็น Null (ซื้อไม่ได้)
        assertNull(expensiveProp.getOwner(), "Player shouldn't be able to buy if broke");
    }

    @Test
    void testPayRent_And_RefuseBuyout() {
        // จำลอง Input: "n" (ปฏิเสธการ Buyout หลังจากจ่ายค่าเช่า)
        provideInput("n\n");
        GameController interactiveController = new GameController();

        List<Player> players = interactiveController.getPlayers();
        Player owner = players.get(0); // Player 1
        Player tenant = players.get(1); // Player 2

        Property property = new Property("Rental Room", 200, 50, 2); // ค่าเช่า 50
        property.setOwner(owner);

        int initialTenantMoney = tenant.getMoney();
        int initialOwnerMoney = owner.getMoney();

        // Player 2 เดินมาตกที่ของ Player 1
        interactiveController.handlePropertySquare(tenant, property);

        // ตรวจสอบการโอนเงินค่าเช่า
        assertEquals(initialTenantMoney - 2, tenant.getMoney(), "Tenant should lose rent money");
        assertEquals(initialOwnerMoney + 2, owner.getMoney(), "Owner should receive rent money");

        // ตรวจสอบว่าเจ้าของยังเป็นคนเดิม (เพราะตอบ n ไม่ Buyout)
        assertEquals(owner, property.getOwner());
    }

    @Test
    void testUpgradeLandmark_Success() {
        // จำลอง Input: "y" (ตกลงอัพเกรด)
        provideInput("y\n");
        GameController interactiveController = new GameController();

        Player owner = interactiveController.getCurrentPlayer();
        Property property = new Property("My Home", 100, 10, 5); // Upgrade cost = 100
        property.setOwner(owner); // เป็นเจ้าของแล้ว

        owner.addMoney(1000); // มีเงินพอ

        // เจ้าของเดินมาตกที่ตัวเอง
        interactiveController.handlePropertySquare(owner, property);

        assertTrue(property.isLandmark(), "Property should become a Landmark");
    }
}
