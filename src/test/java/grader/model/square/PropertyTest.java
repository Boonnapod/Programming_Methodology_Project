package grader.model.square;

import model.Player;
import model.squares.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyTest {

    private Property property;
    private Player owner;
    private Player visitor;

    @BeforeEach
    void setUp() {
        // Property ราคา 200 ค่าเช่า 100
        property = new Property("TestCity", 1, 200, 100);
        owner = new Player("Owner");
        visitor = new Player("Visitor");
    }

    // ✅ 1. ทดสอบ constructor
    @Test
    void testPropertyInitialization() {
        assertEquals(200, property.getPrice());
        assertEquals(400, property.getUpgradeCost()); // price * 2
        assertNull(property.getOwner());
        assertFalse(property.isLandmark());
    }

    // ✅ 2. ทดสอบค่าเช่าปกติ
    @Test
    void testCalculateRentNormal() {
        int rent = property.calculateRent(0);
        assertEquals(100, rent);
    }

    // ✅ 3. ทดสอบค่าเช่าเมื่อเป็น Landmark
    @Test
    void testCalculateRentLandmark() {
        property.setLandmark(true);
        int rent = property.calculateRent(0);
        assertEquals(300, rent); // 100 * 3
    }

    // ✅ 4. ผู้เล่นมาตกที่ดินที่ไม่มีเจ้าของ
    @Test
    void testLandOnUnownedProperty() {
        assertDoesNotThrow(() -> property.landOn(visitor));
    }

    // ✅ 5. เจ้าของมาตกช่องตัวเอง
    @Test
    void testLandOnOwnedBySelf() {
        property.setOwner(owner);
        assertDoesNotThrow(() -> property.landOn(owner));
    }

    // ✅ 6. ผู้เล่นอื่นมาตกที่ดินที่มีเจ้าของ
    @Test
    void testLandOnOwnedByOther() {
        property.setOwner(owner);
        assertDoesNotThrow(() -> property.landOn(visitor));
    }
}
