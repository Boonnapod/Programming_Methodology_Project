package model.cards;

import model.Board;
import model.Card;
import model.Player;
import model.AirplaneSquare; // เพิ่ม import สำหรับ AirplaneSquare ถ้ามีคลาสนี้

import java.util.List;

public class MoveToAirPlane extends Card {

    // ไม่จำเป็นต้องใช้ List<Player> แล้ว เพราะไม่ได้คำนวณคู่แข่ง
    // แต่คงไว้ตามโครงสร้างเดิมถ้า CardManager ยังส่งมา
    public MoveToAirPlane(String description, List<Player> players) {
        super(description);
    }

    @Override
    public void execute(Player currentPlayer, Board board) {
        final int AIRPLANE_POSITION = 15; // ตำแหน่งของ Airport (ตาม Board.java)

        System.out.println(">> Card Action: " + getDescription());
        System.out.println(">> Warping " + currentPlayer.getName() + " directly to the Airport!");

        // 1. ใช้ warp() เพื่อกระโดดไปที่ตำแหน่ง Airport (ไม่ผ่าน Go)
        currentPlayer.warp(AIRPLANE_POSITION);

        // 2. กระตุ้น Logic เมื่อลงบนช่อง Airport
        // Note: Logic การเลือกปลายทางจะถูกจัดการใน handleLandedSquareAction ของ GameController
        // โดยการเรียก landOn() จะเป็นการเริ่มต้น Logic ของช่อง Airport
        board.getSquare(currentPlayer.getPosition()).landOn(currentPlayer);

        // เนื่องจาก AirplaneSquare.landOn() ทำแค่แสดงข้อความ "Choose your destination!"
        // GameController.handleLandedSquareAction จะเห็นว่าลง AirplaneSquare
        // และเรียก handleAirplaneSquare(currentPlayer) ต่อไป

        System.out.println(">> " + currentPlayer.getName() + " is now at position " + AIRPLANE_POSITION + " (Airport).");
    }
}