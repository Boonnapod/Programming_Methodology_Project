package game;

import model.*;
import model.cards.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final CardManager cardManager;
    private int currentPlayerIndex;
    private final Scanner scanner;

    public GameController() {
        this.board = new Board();
        this.players = new ArrayList<>();
        players.add(new Player("Player 1", 1500));
        players.add(new Player("Player 2", 1500));

        this.dice = new Dice();
        this.cardManager = new CardManager(this.players);
        this.currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    public void setupGame() {
        System.out.println("--- Inverse Monopoly Game Setup (20 Squares) ---");
        System.out.println("Goal: Be the first player to go BANKRUPT!");

        System.out.println("Game started with " + players.size() + " players.");
        players.forEach(p -> System.out.println("- " + p.getName() + " (Start Money: $" + p.getMoney() + ")"));
    }

    public void startGameLoop() {
        setupGame();

        int turnCount = 0;
        // ไม่จำกัดรอบในการเล่น: วนไปเรื่อยๆ จนกว่าจะมีผู้เล่นเหลือคนเดียว (ผู้แพ้)
        while (players.stream().filter(p -> p.getMoney() > 0).count() > 1) {

            Player currentPlayer = getNextPlayer();
            if (currentPlayer == null) break;

            System.out.println("\n--- Turn " + (++turnCount) + ": " + currentPlayer.getName()
                    + " (Money: $" + currentPlayer.getMoney()
                    + ", Pos: " + currentPlayer.getPosition() + ") ---");

            // *** Bug Fix: ลบบรรทัดรอ Enter ก่อนเริ่มเทิร์นเพื่อแก้ปัญหา Enter 2 ครั้ง ***
            // โปรแกรมจะรอ Input เมื่อ Logic ต้องการ (ซื้อ, ออกคุก, เครื่องบิน)
            System.out.println("Press Enter to start turn...");
            scanner.nextLine();

            boolean turnPlayed = false;

            if (currentPlayer.getSkipTurns() > 0) {
                handleSkipTurn(currentPlayer);
                turnPlayed = true;
            } else if (currentPlayer.isInJail()) {
                // Bug Fix: handleJailTurn คืนค่า boolean เพื่อบอกว่าผู้เล่นควรเล่นเทิร์นปกติหรือไม่
                if (handleJailTurn(currentPlayer)) {
                    takeNormalTurn(currentPlayer);
                    turnPlayed = true;
                }
            }

            // Bug Fix: เล่นเทิร์นปกติก็ต่อเมื่อยังไม่มีการดำเนินการใดๆ (เช่น ออกจากคุก)
            if (!turnPlayed) {
                takeNormalTurn(currentPlayer);
            }

            // Logic ผู้ชนะ: ตรวจสอบการล้มละลายหลังจบเทิร์น
            if (currentPlayer.getMoney() <= 0) {
                handleBankruptcy(currentPlayer);
            } else {
                // ถ้าไม่ล้มละลาย ให้เลื่อน index ไปผู้เล่นคนถัดไป
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        // Logic ผู้ชนะ (Inverse Monopoly Final Check)
        System.out.println("\n--- Game Over ---");
        Player loser = players.stream().filter(p -> p.getMoney() > 0).findFirst().orElse(null);
        if (loser != null) {
            System.out.println("The last player standing is the LOSER: " + loser.getName());
            System.out.println("🎉 The winner is the first player who went BANKRUPT! 🎉");
        } else {
            System.out.println("Error: Game finished with no players left.");
        }
    }

    private Player getNextPlayer() {
        int initialIndex = currentPlayerIndex;
        do {
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
            if (!players.isEmpty()) {
                return players.get(currentPlayerIndex);
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (currentPlayerIndex != initialIndex);

        return null;
    }

    // *** NEW: Method for GUI to get the current player (Fixes Error 1 & 12) ***
    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    // *** NEW: Method for GUI to advance to the next player (Fixes Error 3, 4, 5, 6, 7, 8, 9, 11) ***
    public void moveToNextPlayer() {
        if (players.isEmpty()) return;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * เมธอดสำหรับจัดการการล้มละลาย (Landmark ถูกปล่อยคืนสู่กระดาน)
     */
    private void handleBankruptcy(Player player) {
        System.out.println("******************************************");
        System.out.println(player.getName() + " is BANKRUPT!");
        System.out.println("******************************************");

        System.out.println("🎉 CONGRATULATIONS TO " + player.getName() + " - You won by going broke! 🎉");

        // ปล่อยทรัพย์สินคืนสู่กระดาน (รวมถึง Landmark)
        board.getSquares().stream()
                .filter(s -> s instanceof Property)
                .map(s -> (Property) s)
                .filter(p -> p.getOwner() == player)
                .forEach(p -> {
                    p.setOwner(null);
                    p.setLandmark(false); // ลบสถานะ Landmark
                    System.out.println("Property " + p.getName() + " returned to board.");
                });

        players.remove(player);

        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
    }

    private void handleSkipTurn(Player player) {
        System.out.println(player.getName() + " must skip this turn due to Card Action. Turns left to skip: " + (player.getSkipTurns() - 1));
        player.setSkipTurns(player.getSkipTurns() - 1);
    }

    /**
     * จัดการ Logic การอยู่ในคุก
     * @return true ถ้าผู้เล่นจ่ายเงินและต้องเล่นเทิร์นปกติในรอบนี้
     */
    private boolean handleJailTurn(Player player) {
        System.out.println(player.getName() + " is in Jail. Pay $50 to get out (y/n)?");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y")) {
            if (player.getMoney() >= 50 && player.deductMoney(50)) {

                player.releaseFromJail();
                System.out.println(player.getName() + " paid $50 to get out of Jail.");
                return true; // แจ้งว่าผู้เล่นต้องเล่นเทิร์นปกติ

            } else {
                System.out.println(player.getName() + " cannot afford to pay $50 and stays in Jail.");
            }
        } else {
            System.out.println(player.getName() + " stays in Jail for this turn.");
        }
        return false; // แจ้งว่าผู้เล่นไม่ได้เล่นเทิร์นปกติ
    }

    private void takeNormalTurn(Player player) {
        int totalRoll = dice.roll();
        System.out.println(player.getName() + " rolled (" + totalRoll + ")");

        player.move(totalRoll);

        handleLandedSquareAction(player);
    }

    private void handleLandedSquareAction(Player player) {
        Square landedSquare = board.getSquare(player.getPosition());
        System.out.println("Landed on: " + landedSquare.getName() + " (Position: " + landedSquare.getPosition() + ")");

        if (landedSquare instanceof CardSquare) {
            Card drawnCard = cardManager.drawCard();
            drawnCard.execute(player, board);
        } else if (landedSquare instanceof AirplaneSquare) {
            handleAirplaneSquare(player);
        } else if (landedSquare instanceof Property) {
            handlePropertySquare(player, (Property) landedSquare);
        } else {
            // *** Bug Fix: เรียก landOn() สำหรับช่องทั่วไป (Tax, Lottery, Go, Jail, Go To Jail) ***
            landedSquare.landOn(player);
        }
    }

    /**
     * เมธอดสำหรับจัดการ Logic การซื้อ/เช่า/อัพเกรด/Buyout Property
     */
    private void handlePropertySquare(Player player, Property property) {
        Player owner = property.getOwner();

        // 1. Logic การจ่ายค่าเช่า (และยึดทรัพย์สิน/Buyout)
        if (owner != null && owner != player) {
            int rent = property.calculateRent(0);
            System.out.println(player.getName() + " must pay rent: $" + rent + " to " + owner.getName());

            // 1.1 จ่ายค่าเช่า
            if (player.deductMoney(rent)) {
                owner.addMoney(rent);

                // 1.2 เสนอโอกาสในการซื้อทรัพย์สินต่อ (Buyout)

                // *** เงื่อนไข: ห้ามเสนอ Buyout หากทรัพย์สินนั้นเป็น Landmark ***
                if (property.isLandmark()) {
                    System.out.println(property.getName() + " is a Landmark and cannot be bought out.");
                    return;
                }

                int buyoutPrice = (int)( property.getPrice() * 1.5);

                if (player.getMoney() >= buyoutPrice) {
                    System.out.println("\n*** BUYOUT OPPORTUNITY ***");
                    System.out.println("Do you want to buy " + property.getName() + " from " + owner.getName() + " for $" + buyoutPrice + "? (y/n)");

                    String buyoutChoice = scanner.nextLine().toLowerCase();

                    if (buyoutChoice.equals("y")) {
                        if (player.deductMoney(buyoutPrice)) {
                            owner.addMoney(buyoutPrice);

                            // โอนทรัพย์สิน
                            property.setOwner(player);
                            // ลบสถานะ Landmark ของเจ้าของเดิมออก
                            property.setLandmark(false);
                            System.out.println("✅ " + player.getName() + " successfully bought out " + property.getName() + "!");
                        }
                    }
                }
            } else {
                // หากเงินไม่พอจ่ายค่าเช่า ถือว่าล้มละลายทันที
                System.out.println(player.getName() + " cannot pay rent and goes BANKRUPT!");
                player.addMoney(-player.getMoney()); // ตั้งเงินเป็น 0
            }
        }

        // 2. Logic การอัพเกรดเป็น Landmark (เมื่อเจ้าของมาตกช่องตัวเองซ้ำ)
        else if (owner == player) {
            if (!property.isLandmark()) {
                if (player.getMoney() >= property.getUpgradeCost()) {
                    System.out.println("\n*** LANDMARK UPGRADE AVAILABLE ***");
                    System.out.println(property.getName() + " can be upgraded to Landmark for $" + property.getUpgradeCost() + ".");
                    System.out.print("Do you want to upgrade? (y/n): ");
                    String upgradeChoice = scanner.nextLine().toLowerCase();

                    if (upgradeChoice.equals("y")) {
                        if (player.deductMoney(property.getUpgradeCost())) {
                            property.setLandmark(true);
                            System.out.println("🎉 " + property.getName() + " is now a LANDMARK! 🎉");
                            System.out.println("New rent for opponents: $" + property.calculateRent(0));
                        }
                    }
                } else {
                    System.out.println("You cannot afford to upgrade to Landmark ($" + property.getUpgradeCost() + ").");
                }
            } else {
                System.out.println(property.getName() + " is already a Landmark.");
            }
        }

        // 3. Logic การซื้อทรัพย์สิน (สำหรับช่องที่ไม่มีเจ้าของ)
        else if (owner == null) {
            if (player.getMoney() >= property.getPrice()) {
                System.out.println(player.getName() + " (Money: $" + player.getMoney() + ") do you want to buy " + property.getName() + " for $" + property.getPrice() + "? (y/n)");
                String buyChoice = scanner.nextLine().toLowerCase();

                if (buyChoice.equals("y")) {
                    if (player.deductMoney(property.getPrice())) {
                        property.setOwner(player);
                        System.out.println(player.getName() + " now owns " + property.getName());
                    }
                }
            } else {
                System.out.println("Cannot afford to buy " + property.getName() + " ($" + property.getPrice() + ")");
            }
        }
    }


    private void handleAirplaneSquare(Player player) {
        List<Square> squares = board.getSquares();

        System.out.println("\n--- AIRPLANE TRAVEL ---");
        System.out.println("Available destinations (0-19):");
        for (Square s : squares) {
            System.out.println("[" + s.getPosition() + "] " + s.getName());
        }

        int targetPosition = -1;
        while (targetPosition < 0 || targetPosition >= 20) {
            try {
                System.out.print("Enter destination position (0-19) to fly to: ");
                targetPosition = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                targetPosition = -1;
            }
        }

        handleAirplaneAction(player, targetPosition); // Call new centralized logic

        // ลงบนช่องที่เลือก
        handleLandedSquareAction(player);
    }

    // *** NEW: Method for GUI to handle movement during Airplane Square (Fixes Error 10) ***
    public void handleAirplaneAction(Player player, int targetPosition) {
        System.out.println("GUI: Flying " + player.getName() + " to " + board.getSquare(targetPosition).getName() + "...");

        int currentPos = player.getPosition();
        int boardSize = board.getBoardSize();

        // Calculate steps, ensuring it moves forward, wrapping around the board
        int steps = targetPosition - currentPos;
        if (steps <= 0) {
            steps += boardSize; // Assumes board size is 20
        }

        // Use move() to ensure Go money is collected if passed.
        player.move(steps);
        // The GUI (GameBoardScreen) is responsible for calling handleLandedSquareInteraction() after this returns.
    }


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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Scanner getScanner() {
        return scanner;
    }
}