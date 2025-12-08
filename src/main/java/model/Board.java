package model;

import model.squares.*;

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

        // 2. กำหนดช่องสำคัญ (Special Squares)
        squares.set(0, new GoSquare(0)); // 0: Go

        // ****************************************************************
        // 3. แก้ไขตำแหน่ง 5 (Jail): ให้เป็นกับดัก! เดินตกปุ๊บ ติดคุกทันที
        // (ใช้ Anonymous Class override landOn เพื่อสั่ง goToJail)
        // ****************************************************************
        squares.set(5, new JailSquare("Jail", 5) {
            @Override
            public void landOn(Player player) {
                System.out.println(player.getName() + " landed on Jail.");
                System.out.println(">> YOU ARE ARRESTED!");

                // สั่งให้ผู้เล่นติดคุกทันที (Set jailTurns = 2)
                player.goToJail();
            }
        });

        // ช่อง Lottery (ตำแหน่ง 10)
        squares.set(10, new LotterySquare("Lottery", 10));

        // ช่องการ์ด (CardSquare)
        squares.set(3, new CardSquare("Mystery Card", 3));
        squares.set(8, new CardSquare("Mystery Card", 8));
        squares.set(13, new CardSquare("Mystery Card", 13));

        // ช่องเครื่องบิน (AirplaneSquare)
        squares.set(15, new AirplaneSquare("Airport", 15));

        // ช่อง Tax (จ่ายภาษี)
        squares.set(4, new TaxSquare("Income Tax", 4));

        // 4. กำหนด Property (จังหวัดต่างๆ)
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

        // ****************************************************************
        // 5. แก้ไขตำแหน่ง 19 กลับเป็น BANGKOK ตามที่ต้องการ
        // ****************************************************************
        squares.set(19, new Property("BANGKOK", 19, 300, 150));

        // 6. เติมช่องที่เหลือกัน Null Pointer Exception
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (squares.get(i) == null) {
                squares.set(i, new Property("Square " + i, i, 80, 10));
            }
        }
    }

    public Square getSquare(int index) {
        return squares.get(index % BOARD_SIZE);
    }

    public List<Square> getSquares() {
        return squares;
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }
}