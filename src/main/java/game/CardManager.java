package game;


import model.Card;
import model.Player;
import model.cards.MoveToOpponentBestPropertyCard;
import model.cards.ReceiveMoneyCard;
import model.cards.SkipTurnCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardManager {
    private final List<Card> deck;

    public CardManager(List<Player> players) {
        this.deck = new ArrayList<>();
        initializeDeck(players);
        Collections.shuffle(deck); // สลับการ์ด
    }

    private void initializeDeck(List<Player> players) {
        // 3.1 การ์ดเคลื่อนที่ไปยังเมืองที่แพงที่สุดของฝั่งตรงข้าม
        deck.add(new MoveToOpponentBestPropertyCard("Move to your opponent's most expensive property.", players));

        // 3.2 การ์ดหยุดเดิน 1 ตา
        deck.add(new SkipTurnCard("You've missed your train. Skip your next turn.", 1));

        // 3.3 การ์ดได้รับเงินแบบสุ่ม 10% - 20% ของเงินตอนเริ่มเกม (1500)
        Random random = new Random();
        int amount = Player.getInitialMoney() * (random.nextInt(11) + 10) / 100; // 10% - 20%
        deck.add(new ReceiveMoneyCard("You won the lottery! Collect $" + amount, amount));

        // เพิ่มการ์ดซ้ำเพื่อให้มีจำนวนพอสมควร
        deck.add(new SkipTurnCard("The taxman caught you. Skip next turn.", 1));
        deck.add(new ReceiveMoneyCard("Birthday money! Collect $150.", 150));
        deck.add(new MoveToOpponentBestPropertyCard("Teleportation gone wrong! Jump to foe's best property.", players));
    }

    public Card drawCard() {
        if (deck.isEmpty()) {
            // สร้างกองการ์ดใหม่หากหมด
            System.out.println("Deck is empty, reshuffling new cards.");
            initializeDeck(new ArrayList<>()); // สามารถส่ง Player list ที่ถูกต้องได้ถ้าต้องการ
            Collections.shuffle(deck);
        }

        // จั่วการ์ดจากด้านบน
        Card drawnCard = deck.remove(0);
        deck.add(drawnCard); // ใส่การ์ดกลับเข้ากองด้านล่างสุด
        return drawnCard;
    }
}