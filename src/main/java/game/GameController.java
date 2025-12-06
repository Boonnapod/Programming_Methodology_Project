package game;

import model.*;
import model.cards.*;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final CardManager cardManager;
    private int currentPlayerIndex;
    // ลบ private final Scanner scanner;

    public GameController() {
        this.board = new Board();
        this.players = new ArrayList<>();
        // ใช้ INITIAL_MONEY จาก Player
        players.add(new Player("Player 1", Player.getInitialMoney()));
        players.add(new Player("Player 2", Player.getInitialMoney()));

        this.dice = new Dice();
        this.cardManager = new CardManager(this.players);
        this.currentPlayerIndex = 0;
        // ลบ this.scanner = new Scanner(System.in);
    }

    // *** ลบ setupGame() และ startGameLoop() เนื่องจากย้าย Logic ไปที่ GameBoardScreen ***

    // --- Game Logic Methods ---

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Logic สำหรับ AirplaneSquare ที่รับค่าปลายทางจาก GUI
     * @param player ผู้เล่นที่กำลังบิน
     * @param targetPosition ตำแหน่งปลายทางที่เลือก
     */
    public void handleAirplaneAction(Player player, int targetPosition) {

        int currentPosition = player.getPosition();
        int steps = targetPosition - currentPosition;

        // คำนวณ steps หากต้องวนรอบผ่าน Go
        if (steps <= 0) {
            steps += board.getBoardSize();
        }

        System.out.println(player.getName() + " is flying " + steps + " steps to " + board.getSquare(targetPosition).getName() + "...");

        // ใช้ move() เพื่อให้เก็บเงินผ่าน Go ได้
        player.move(steps);
    }

    // --- Getters ---
    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public Dice getDice() {
        return dice;
    }

    public CardManager getCardManager() {
        return cardManager;
    }
}