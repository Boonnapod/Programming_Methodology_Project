package game;

import game.CardManager;
import game.Dice;
import model.*;
import model.cards.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // ลบออกไปแล้ว แต่คงไว้ใน package

public class GameController {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final CardManager cardManager;
    private int currentPlayerIndex;
    // private final Scanner scanner; // ลบออก

    public GameController() {
        this.board = new Board();
        // สร้างผู้เล่นเริ่มต้น (P1 และ P2) ใน GameController
        this.players = new ArrayList<>();
        players.add(new Player("Player 1", Player.getInitialMoney()));
        players.add(new Player("Player 2", Player.getInitialMoney()));

        this.dice = new Dice();
        this.cardManager = new CardManager(this.players); // ส่ง player list ไปให้ CardManager
        this.currentPlayerIndex = 0;
        // this.scanner = new Scanner(System.in); // ลบออก
    }

    /**
     * ดำเนินการเล่น 1 Turn ของผู้เล่นปัจจุบัน
     * เมธอดนี้ถูกเรียกโดย Roll Button ใน UI
     * @return Player ผู้เล่นที่เพิ่งเล่นจบ
     */
    public Player takeNormalTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        int previousPlayerIndex = currentPlayerIndex; // เก็บ Index ผู้เล่นปัจจุบันไว้เพื่อ Return

        System.out.println("\n--- " + currentPlayer.getName() + "'s Turn (Money: $" + currentPlayer.getMoney() + ") ---");

        // 1. ตรวจสอบเงื่อนไขข้ามตา (คุก/ข้ามตา)
        if (currentPlayer.isInJail()) {
            System.out.println(currentPlayer.getName() + " is in Jail. Skipping turn.");
        } else if (currentPlayer.getSkipTurns() > 0) {
            System.out.println(currentPlayer.getName() + " must skip " + currentPlayer.getSkipTurns() + " turn(s).");
            currentPlayer.setSkipTurns(currentPlayer.getSkipTurns() - 1);
        } else {
            // 2. ทอยลูกเต๋า
            int roll = dice.roll();
            System.out.println(currentPlayer.getName() + " rolled a " + roll);

            // 3. เคลื่อนที่
            currentPlayer.move(roll);
            Square landedSquare = board.getSquare(currentPlayer.getPosition());
            System.out.println(currentPlayer.getName() + " landed on [" + landedSquare.getPosition() + "] " + landedSquare.getName());

            // 4. กระตุ้น Logic ของช่อง
            landedSquare.landOn(currentPlayer);

            // 5. จัดการ Action ซื้อ/เช่า (ต้องใช้ Dialog ใน UI)
            if (landedSquare instanceof Property) {
                Property property = (Property) landedSquare;
                if (property.getOwner() == null) {
                    System.out.println("Property is unowned. (UI Action Required: Buy/Skip)");
                }
            }

            // 6. จัดการ CardSquare
            if (landedSquare instanceof CardSquare) {
                Card drawnCard = cardManager.drawCard();
                System.out.println("Drew Card: " + drawnCard.getDescription());
                drawnCard.execute(currentPlayer, board);
            }

            // 7. จัดการ AirplaneSquare
            if (landedSquare instanceof AirplaneSquare) {
                System.out.println("Landed on Airport. (UI Action Required: Choose destination)");
            }

            // 8. ตรวจสอบล้มละลาย (Simplified)
            checkBankruptcy(currentPlayer);
        }

        // 9. เปลี่ยนผู้เล่น
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // 10. Return Player ที่เพิ่งเล่นจบ
        return players.get(previousPlayerIndex);
    }

    private void checkBankruptcy(Player player) {
        if (player.getMoney() <= 0) {
            System.out.println(player.getName() + " is Bankrupt!");
        }
    }

    // --- Getters for UI ---
    public List<Player> getPlayers() { return players; }
    public Board getBoard() { return board; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
}