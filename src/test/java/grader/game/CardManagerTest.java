package grader.game;

import game.CardManager;
import model.Player;
import model.cards.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardManagerTest {

    private CardManager cardManager;

    @BeforeEach
    void setUp() {
        // สร้าง List ผู้เล่นหลอกๆ ขึ้นมา (เพราะ Constructor ต้องการ)
        List<Player> players = new ArrayList<>();
        players.add(new Player("P1"));

        cardManager = new CardManager(players);
    }

    @Test
    void testDrawCard_NotNull() {
        // เทสว่าจั่วออกมาแล้วได้การ์ดจริง ไม่ใช่ null
        Card card = cardManager.drawCard();
        assertNotNull(card, "Draw card should not return null");
    }

    @Test
    void testDrawCard_InfiniteDeck() {
        // เนื่องจาก Logic ของคุณคือการวนการ์ด (เอาออกหัว -> ไปต่อหาง)
        // เราจะลองจั่วเยอะๆ (เช่น 20 ครั้ง) เพื่อดูว่าการ์ดไม่มีวันหมด

        for (int i = 0; i < 20; i++) {
            Card c = cardManager.drawCard();
            assertNotNull(c, "Deck should never be empty due to recycling logic");
        }
    }

    @Test
    void testDeckInitialization() {
        // เทสว่าเริ่มมามีการ์ดเลย (จั่วครั้งแรกต้องได้ของ)
        assertDoesNotThrow(() -> cardManager.drawCard());
    }
}