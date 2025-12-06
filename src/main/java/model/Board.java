package model;

import java.util.ArrayList;
import java.util.List;

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
        squares.set(10, new Square("Free Parking", 10) { // 10: Free Parking
            @Override
            public void landOn(Player player) {
                System.out.println("Landed on Free Parking. Nothing happens.");
            }
        });
        squares.set(15, new GoToJailSquare(15)); // 15: Go to Jail

        // 3. เพิ่มช่องการ์ด (CardSquare) และช่องเครื่องบิน (AirplaneSquare)
        // CardManager จะถูกส่งผ่านไปใน GameController
        squares.set(3, new CardSquare("Chance 1", 3)); // ช่องการ์ด 1
        squares.set(8, new CardSquare("Community Chest", 8)); // ช่องการ์ด 2
        squares.set(13, new CardSquare("Chance 2", 13)); // ช่องการ์ด 3

        squares.set(17, new AirplaneSquare("Airport", 17)); // <--- NEW: ช่องเครื่องบิน

        // 4. ตัวอย่าง Property และ Tax
        squares.set(1, new Property("Brown Street", 1, 60, 2));
        squares.set(4, new Square("Income Tax", 4) { // ตำแหน่ง 4
            @Override
            public void landOn(Player player) {
                System.out.println("Income Tax: Player pays $100.");
                player.deductMoney(100);
            }
        });
        squares.set(6, new Property("Light Blue Road", 6, 60, 4));
        squares.set(11, new Property("Orange Plaza", 11, 100, 10));
        squares.set(16, new Property("Yellow Lane", 16, 100, 10));
        squares.set(19, new Property("Dark Blue Hotel", 19, 200, 25));

        // 5. เติมช่องที่เหลือ
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (squares.get(i) == null) {
                squares.set(i, new Property("Square " + i, i, 80, 8));
            }
        }
    }

    public int getBoardSize(){
        return BOARD_SIZE;
    }
    public Square getSquare(int index) {
        return squares.get(index % BOARD_SIZE);
    }


    public List<Square> getSquares() {
        return squares;
    }
}