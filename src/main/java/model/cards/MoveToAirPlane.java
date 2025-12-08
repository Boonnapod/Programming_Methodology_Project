package model.cards;

import model.Board;
import model.Card;
import model.Player;
import model.Square;

public class MoveToAirPlane extends Card {

    // ตำแหน่ง Airport จาก Board.java คือ index 15
    private static final int AIRPLANE_POSITION = 15;

    // ใช้ constructor ที่รับแค่ description ตาม GameController.zip/CardManager.java
    public MoveToAirPlane(String description) {
        super(description);
    }

    @Override
    public void execute(Player currentPlayer, Board board) {

        System.out.println(">> Card Action: " + getDescription());
        System.out.println(">> " + currentPlayer.getName() + " is teleported to the Airport (Position " + AIRPLANE_POSITION + ")!");

        int currentPos = currentPlayer.getPosition();
        // Board Size คือ 20 (ใช้ค่าคงที่จาก Player หรือกำหนดตรงนี้ถ้าไม่มี getter ใน Board)
        int boardSize = 20;

        // คำนวณจำนวนก้าวที่ต้องเดิน (เพื่อให้ผ่าน Go ได้หากจำเป็น)
        int steps;
        if (currentPos <= AIRPLANE_POSITION) {
            steps = AIRPLANE_POSITION - currentPos;
        } else {
            // เดินวนกระดาน
            steps = boardSize - currentPos + AIRPLANE_POSITION;
        }

        // 1. เคลื่อนที่ผู้เล่นโดยใช้ move(steps) เพื่อจัดการเรื่องผ่าน Go
        // **FIX 1: ใช้ move(steps) ครั้งเดียวแทนการวนลูป**
        currentPlayer.move(steps);

        System.out.println(">> " + currentPlayer.getName() + " is now at position " + currentPlayer.getPosition() + " (" + board.getSquare(currentPlayer.getPosition()).getName() + ").");

        // *** FIX 2: ลบบรรทัดที่เรียก square.landOn(currentPlayer) ออก ***
        // ปล่อยให้ GameController จัดการ Logic การทำงานของช่องใหม่ต่อ
    }
}