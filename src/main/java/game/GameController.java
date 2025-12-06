package game;

import model.*;
import model.cards.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameController {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final CardManager cardManager;
    private int currentPlayerIndex;
    private final Scanner scanner; // ใช้สำหรับการรับ input ใน console (อาจถูกละเว้นใน UI)

    public GameController() {
        this.board = new Board();
        // ต้องสร้าง players ก่อนจึงจะสร้าง CardManager ได้
        this.players = new ArrayList<>();
        // สมมติว่ามี 2 ผู้เล่นสำหรับ UI
        players.add(new Player("Player 1", 1500));
        players.add(new Player("Player 2", 1500));

        this.dice = new Dice();
        this.cardManager = new CardManager(this.players); // ส่ง player list ไปให้ CardManager
        this.currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    // --- Getters ที่ GameBoardScreen ต้องใช้ ---
    public Board getBoard() { return board; }
    public List<Player> getPlayers() { return players; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    // *** NEW: เมธอดที่ GameBoardScreen ใช้ในการทอยลูกเต๋า ***
    public Dice getDice() { return dice; }

    // ---------------------------------------------------------------------------------------
    // *** NEW: เมธอดสำหรับทำ Logic ทั้งหมดหลังผู้เล่นเดินเสร็จแล้ว (LandOn, Check Bankruptcy) ***
    // ---------------------------------------------------------------------------------------
    /**
     * ดำเนินการ Logic ของช่องที่ลงทั้งหมดหลังจากแอนิเมชันการเดินจบลง
     * @param player ผู้เล่นที่จบการเดิน
     */
    public void finalizeTurnLogic(Player player) {
        // 1. ลงบนช่องสุดท้าย (Landed on Square)
        Square landedSquare = board.getSquare(player.getPosition());
        System.out.println(player.getName() + " landed on " + landedSquare.getName());

        // เรียก landOn() ของช่องนั้นๆ เพื่อทำ Logic พื้นฐาน (เช่น จ่ายภาษี, เข้าคุก)
        landedSquare.landOn(player);

        // 2. จัดการ Logic พิเศษตามประเภทของช่องที่ลง (เช่น ซื้อ/เช่า, จั่วการ์ด)
        if (landedSquare instanceof Property) {
            handlePropertyAction(player, (Property) landedSquare);
        } else if (landedSquare instanceof CardSquare) {
            handleCardAction(player);
        } else if (landedSquare instanceof AirplaneSquare) {
            // ต้องมี Logic ให้ผู้เล่นเลือกปลายทางใน UI
            handleAirplaneSquare(player);
        }

        // 3. ตรวจสอบการล้มละลาย
        checkBankruptcy(player);

        // 4. ส่งไม้ต่อให้ผู้เล่นถัดไป
        moveToNextPlayer();
    }

    // --- Helper Logic Methods ---

    private void handlePropertyAction(Player player, Property property) {
        if (property.getOwner() == null) {
            // Logic การซื้อ: ใน UI จะต้องแสดงปุ่ม "Buy" หรือ "Auction"
            System.out.println(player.getName() + " landed on unowned property: " + property.getName() + ". (Price: $" + property.getPrice() + ")");
        }
        // Logic การจ่ายค่าเช่า ถูกจัดการแล้วใน Property.landOn()
    }

    private void handleCardAction(Player player) {
        Card card = cardManager.drawCard();
        // แสดงคำอธิบายการ์ด
        System.out.println("You drew: " + card.getDescription());
        // ดำเนินการตามการ์ด
        card.execute(player, board);
    }

    private void handleAirplaneSquare(Player player) {
        System.out.println(player.getName() + " landed on Airport. Available positions:");

        // แสดงช่องทั้งหมดเพื่อเป็นทางเลือกในการบิน
        board.getSquares().stream()
                .filter(s -> s.getPosition() != player.getPosition())
                .forEach(s -> System.out.println("[" + s.getPosition() + "] " + s.getName()));

        int targetPosition = -1;

        // ** NOTE: ใน UI จริงจะต้องเปลี่ยน Logic ส่วนนี้เป็นการแสดง Pop-up ให้ผู้เล่นเลือก
        // แต่สำหรับตอนนี้ ให้เลือกตำแหน่ง 0 ชั่วคราว (Go) เพื่อให้โค้ดทำงานต่อไปได้
        targetPosition = 0;

        if (targetPosition != -1) {
            int steps = targetPosition - player.getPosition();
            if (steps <= 0) {
                steps += board.getBoardSize(); // วนรอบผ่าน Go
            }

            System.out.println("Flying " + steps + " steps to " + board.getSquare(targetPosition).getName() + "...");

            // ใช้ move() เพื่อให้เก็บเงินผ่าน Go ได้ หาก targetPosition < currentPosition
            player.move(steps);

            // ลงบนช่องที่เลือก (เรียก logic ซ้ำเพื่อให้เกิด action ของช่องนั้น)
            Square landedSquare = board.getSquare(player.getPosition());
            landedSquare.landOn(player);

            // ต้องเรียก Logic ลงช่องซ้ำหลังจาก Move เสร็จแล้ว
            if (landedSquare instanceof Property) {
                handlePropertyAction(player, (Property) landedSquare);
            } else if (landedSquare instanceof CardSquare) {
                handleCardAction(player);
            }
        }
    }

    private void checkBankruptcy(Player player) {
        if (player.getMoney() < 0) {
            System.out.println("!!! " + player.getName() + " is BANKRUPT and loses the game. All other players win!");
            // ใน UI จริงจะมีการแสดงหน้าจอจบเกม
        }
    }

    public void moveToNextPlayer() {
        int initialIndex = currentPlayerIndex;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            Player nextPlayer = players.get(currentPlayerIndex);

            // 1. เช็คข้ามตา
            if (nextPlayer.getSkipTurns() > 0) {
                System.out.println(nextPlayer.getName() + " skips their turn! (" + nextPlayer.getSkipTurns() + " turns remaining)");
                nextPlayer.setSkipTurns(nextPlayer.getSkipTurns() - 1);
                continue;
            }

            // 2. เช็คคุก
            if (nextPlayer.isInJail()) {
                System.out.println(nextPlayer.getName() + " is in Jail. Skip turn.");
                continue;
            }

            // 3. ตรวจสอบว่าล้มละลายหรือไม่
            if (nextPlayer.getMoney() < 0) {
                System.out.println(nextPlayer.getName() + " is already bankrupt. Skipping.");
                continue;
            }

            // ถ้าผ่านทุกเงื่อนไข ถือว่าเป็นผู้เล่นคนถัดไป
            return;

        } while (currentPlayerIndex != initialIndex);

        System.out.println("No more active players. Game ends.");
    }
}