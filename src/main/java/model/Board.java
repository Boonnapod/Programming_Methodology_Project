package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// import com.monopoly.game.CardManager; // ถูกนำออกเนื่องจากไม่ได้ใช้ใน Board.java แต่ถูกคงไว้ถ้าคุณต้องการ

public class Board {
    private final List<Square> squares;
    private static final int BOARD_SIZE = 20;

    public Board() {
        this.squares = new ArrayList<>(BOARD_SIZE);
        initializeBoard();
    }

    private void initializeBoard() {
        // 1. สร้าง ArrayList ให้มีขนาด 20 ช่อง
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares.add(null);
        }

        // 2. กำหนดช่องสำคัญโดยใช้ set(index, element)
        squares.set(0, new GoSquare(0)); // 0: Go
        squares.set(5, new JailSquare("Jail (Just Visiting)", 5)); // 5: Jail (มุม)

        // **********************************************
        // ******* LOGIC แก้ไขสำหรับตำแหน่ง 10 (Lottery) *******
        squares.set(10, new Square("Lottery", 10) {
            @Override
            public void landOn(Player player) {
                // Logic: สุ่มตัวเลข 1-5 แล้วคูณด้วย 100 (ได้ 100, 200, 300, 400, หรือ 500)
                Random random = new Random();
                int amount = (random.nextInt(5) + 1) * 100;

                System.out.println(player.getName() + " landed on Lottery! You win $" + amount + "!");
                player.addMoney(amount);
            }
        });
        // **********************************************

        // 3. เพิ่มช่องการ์ด (CardSquare) และช่องเครื่องบิน (AirplaneSquare)
        squares.set(3, new CardSquare("Mystery Card", 3)); // ช่องการ์ด 1
        squares.set(8, new CardSquare("Mystery Card", 8)); // ช่องการ์ด 2
        squares.set(13, new CardSquare("Mystery Card", 13)); // ช่องการ์ด 3

        // Note: index 15 ควรเป็น GoToJail ตามโค้ดเดิม แต่ใช้ Airport ตามที่คุณให้มา
        squares.set(15, new AirplaneSquare("Airport", 15));

        // 4. ตัวอย่าง Property และ Tax

        squares.set(4, new Square("Income Tax", 4) { // ตำแหน่ง 4
            @Override
            public void landOn(Player player) {
                System.out.println("Income Tax: Player pays $300.");
                player.deductMoney(300);
            }
        });
        squares.set(1, new Property("UDONTHANI", 1, 200, 100));
        squares.set(2, new Property("CHAIYAPHUM", 2, 220, 110));
        squares.set(6, new Property("BURIRAM", 6, 180, 90));
        squares.set(7, new Property("CHAI NAT", 7, 260, 130));

        squares.set(9, new Property("KRABI", 9, 240, 120));
        squares.set(11, new Property("LOPBURI", 11, 180, 90));
        squares.set(12, new Property("PATTAYA", 12, 210, 105));
        squares.set(14, new Property("PATTANI", 14, 250, 125));

        squares.set(16, new Property("AYUTTHAYA", 16, 250, 125));
        squares.set(17, new Property("SARABURI", 17, 270, 135));
        squares.set(18, new Property("YALA", 18, 280, 140));
        squares.set(19, new Property("BANGKOK", 19, 300, 150));

    }

    public Square getSquare(int index) {
        return squares.get(index % BOARD_SIZE);
    }

    public List<Square> getSquares() {
        return squares;
    }
    public int getBoardSize(){
        return BOARD_SIZE;
    }
}