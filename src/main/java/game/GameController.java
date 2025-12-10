package game;

import model.*;
import model.cards.*;
import model.squares.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main controller class for the Inverse Monopoly game.
 * <p>
 * This class handles the core game loop, player turns, board interactions,
 * and game state management including win/loss conditions (Bankruptcy).
 * </p>
 */
public class GameController {
    private final Board board;
    private final Dice dice;
    private final List<Player> players;
    private final CardManager cardManager;
    private int currentPlayerIndex;
    private final Scanner scanner;

    /**
     * Initializes the game controller, sets up the board, players, dice, and card manager.
     */
    public GameController() {
        this.board = new Board();
        this.players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));

        this.dice = new Dice();
        this.cardManager = new CardManager(this.players);
        this.currentPlayerIndex = 0;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the initial game setup information and lists the participating players.
     */
    public void setupGame() {
        System.out.println("--- Inverse Monopoly Game Setup (20 Squares) ---");
        System.out.println("Goal: Be the first player to go BANKRUPT!");

        System.out.println("Game started with " + players.size() + " players.");
        players.forEach(p -> System.out.println("- " + p.getName() + " (Start Money: $" + p.getMoney() + ")"));
    }

    /**
     * Starts the main game loop.
     * <p>
     * The loop continues until the win condition is met (only one player remains with money,
     * or a player goes bankrupt depending on the game rules).
     * </p>
     */
    public void startGameLoop() {
        setupGame();

        int turnCount = 0;
        // Game continues until specific conditions are met
        while (players.stream().filter(p -> p.getMoney() > 0).count() > 1) {

            Player currentPlayer = getNextPlayer();
            if (currentPlayer == null) break;

            System.out.println("\n--- Turn " + (++turnCount) + ": " + currentPlayer.getName()
                    + " (Money: $" + currentPlayer.getMoney()
                    + ", Pos: " + currentPlayer.getPosition() + ") ---");

            // Check skip turn status
            if (currentPlayer.getSkipTurns() > 0) {
                handleSkipTurn(currentPlayer);
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                continue;
            }

            System.out.println("Press Enter to start turn...");
            scanner.nextLine();

            boolean turnPlayed = false;

            if (currentPlayer.getSkipTurns() > 0) {
                handleSkipTurn(currentPlayer);
                turnPlayed = true;
            } else if (currentPlayer.isInJail()) {
                if (handleJailTurn(currentPlayer)) {
                    if(!currentPlayer.isInJail())
                        takeNormalTurn(currentPlayer);
                    turnPlayed = true;
                }
            }

            if (!turnPlayed) {
                takeNormalTurn(currentPlayer);
            }

            // Check for bankruptcy after turn
            if (currentPlayer.getMoney() <= 0) {
                handleBankruptcy(currentPlayer);
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        // End game logic
        System.out.println("\n--- Game Over ---");
        Player loser = players.stream().filter(p -> p.getMoney() > 0).findFirst().orElse(null);
        if (loser != null) {
            System.out.println("The last player standing is the LOSER: " + loser.getName());
            System.out.println("🎉 The winner is the first player who went BANKRUPT! 🎉");
        } else {
            System.out.println("Error: Game finished with no players left.");
        }
    }

    /**
     * Retrieves the next valid player in the turn rotation.
     *
     * @return The next {@link Player} object, or null if no players are available.
     */
    public Player getNextPlayer() {
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

    /**
     * Gets the player whose turn it currently is.
     * Used primarily by the GUI to update the display.
     *
     * @return The current {@link Player}.
     */
    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    /**
     * Advances the game to the next player's turn manually.
     */
    public void moveToNextPlayer() {
        if (players.isEmpty()) return;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Handles the bankruptcy process for a player.
     * <p>
     * This method announces the winner/loser, returns the player's properties to the board,
     * resets property statuses (removes landmarks), and removes the player from the game.
     * </p>
     *
     * @param player The player who has gone bankrupt.
     */
    public void handleBankruptcy(Player player) {
        System.out.println("******************************************");
        System.out.println(player.getName() + " is BANKRUPT!");
        System.out.println("******************************************");

        System.out.println("🎉 CONGRATULATIONS TO " + player.getName() + " - You won by going broke! 🎉");

        // Return assets to board
        board.getSquares().stream()
                .filter(s -> s instanceof Property)
                .map(s -> (Property) s)
                .filter(p -> p.getOwner() == player)
                .forEach(p -> {
                    p.setOwner(null);
                    p.setLandmark(false);
                    System.out.println("Property " + p.getName() + " returned to board.");
                });

        players.remove(player);

        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0;
        }
    }

    /**
     * Decrements the skip turn counter for a player and notifies them.
     *
     * @param player The player skipping the turn.
     */
    public void handleSkipTurn(Player player) {
        System.out.println(player.getName() + " must skip this turn due to Card Action. Turns left to skip: " + (player.getSkipTurns() - 1));
        player.setSkipTurns(player.getSkipTurns() - 1);
    }

    /**
     * Manages the turn logic when a player is in Jail.
     * Offers the player a choice to pay to get out.
     *
     * @param player The player in jail.
     * @return {@code true} if the player paid and should take a normal turn immediately; {@code false} otherwise.
     */
    public boolean handleJailTurn(Player player) {
        System.out.println(player.getName() + " is in Jail. Pay $50 to get out (y/n)?");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("y")) {
            if (player.getMoney() >= 50 && player.deductMoney(50)) {
                player.releaseFromJail();
                System.out.println(player.getName() + " paid $50 to get out of Jail.");
                return true;
            } else {
                System.out.println(player.getName() + " cannot afford to pay $50 and stays in Jail.");
            }
        } else {
            System.out.println(player.getName() + " stays in Jail for this turn.");
        }
        return false;
    }

    /**
     * Executes a normal turn for a player: rolling dice, moving, and interacting with the landed square.
     *
     * @param player The current player.
     */
    public void takeNormalTurn(Player player) {
        int totalRoll = dice.roll();
        System.out.println(player.getName() + " rolled (" + totalRoll + ")");

        player.move(totalRoll);
        handleLandedSquareAction(player);
    }

    /**
     * Identifies the type of square the player landed on and triggers the appropriate action.
     * Supports Card squares, Property squares, Airplane squares, and standard squares.
     *
     * @param player The player who just moved.
     */
    /**
     * Identifies the type of square the player landed on and triggers the appropriate action.
     * Supports Card squares, Property squares, Airplane squares, and standard squares.
     * * [UPDATED] Added support for Rentable interface.
     *
     * @param player The player who just moved.
     */
    public void handleLandedSquareAction(Player player) {
        Square landedSquare = board.getSquare(player.getPosition());
        System.out.println("Landed on: " + landedSquare.getName() + " (Position: " + landedSquare.getPosition() + ")");

        if (landedSquare instanceof CardSquare) {
            Card drawnCard = cardManager.drawCard();
            int oldPosition = player.getPosition();

            drawnCard.execute(player, board);

            // Re-handle if the card moved the player to a new square
            if (player.getPosition() != oldPosition) {
                System.out.println(">> Card action moved " + player.getName() + ". Re-handling action for the new square.");
                handleLandedSquareAction(player);
            }

        } else if (landedSquare instanceof Property) {
            // Property implements Rentable, but has specific logic (Landmarks/Buyout)
            // so we handle it specifically here first.
            handlePropertySquare(player, (Property) landedSquare);

        } else if (landedSquare instanceof AirplaneSquare) {
            handleAirplaneSquare(player);

        } else if (landedSquare instanceof Rentable) {
            // *** NEW: Check for generic Rentable squares (like IslandSquare) ***
            handleRentableSquare(player, (Square) landedSquare);

        } else {
            landedSquare.landOn(player);
        }
    }

    /**
     * [NEW METHOD] Handles generic Rentable squares (e.g. IslandSquare).
     * Since Rentable interface only has calculateRent(), we might need to cast
     * to specific types to handle ownership/buying if the interface doesn't support it.
     */
    public void handleRentableSquare(Player player, Square square) {
        // Cast to Rentable interface to use its method
        Rentable rentable = (Rentable) square;

        // Example logic: Just print the calculated rent for now
        // In a full implementation, you would check ownership here similar to Property.
        // Since Rentable interface doesn't strictly define getOwner(),
        // we usually delegate the logic to the square's landOn() or cast to specific types.

        System.out.println("[Rentable Check] This square charges rent!");

        // เรียกใช้ landOn ตามปกติเพื่อให้ Square จัดการ Logic ของตัวเอง (เช่น การจ่ายค่าเช่า/การซื้อ)
        // ซึ่งภายใน class นั้น (เช่น IslandSquare) ควรจะมีการเรียกใช้ calculateRent() ของตัวเอง
        square.landOn(player);
    }

    /**
     * Handles interactions with a Property square (Rent, Buyout, Upgrade, Purchase).
     *
     * @param player   The player landing on the property.
     * @param property The property square.
     */
    public void handlePropertySquare(Player player, Property property) {
        Player owner = property.getOwner();

        if (owner != null && owner != player) {
            // Pay Rent & Buyout Logic
            int rent = property.calculateRent(0);
            System.out.println(player.getName() + " must pay rent: $" + rent + " to " + owner.getName());

            if (player.deductMoney(rent)) {
                owner.addMoney(rent);

                if (property.isLandmark()) {
                    System.out.println(property.getName() + " is a Landmark and cannot be bought out.");
                    return;
                }

                int buyoutPrice = (int)(property.getPrice() * 1.5);
                if (player.getMoney() >= buyoutPrice) {
                    System.out.println("\n*** BUYOUT OPPORTUNITY ***");
                    System.out.println("Do you want to buy " + property.getName() + " from " + owner.getName() + " for $" + buyoutPrice + "? (y/n)");

                    String buyoutChoice = scanner.nextLine().toLowerCase();
                    if (buyoutChoice.equals("y")) {
                        if (player.deductMoney(buyoutPrice)) {
                            owner.addMoney(buyoutPrice);
                            property.setOwner(player);
                            property.setLandmark(false);
                            System.out.println("✅ " + player.getName() + " successfully bought out " + property.getName() + "!");
                        }
                    }
                }
            } else {
                System.out.println(player.getName() + " cannot pay rent and goes BANKRUPT!");
                player.addMoney(-player.getMoney());
            }
        } else if (owner == player) {
            // Upgrade Logic
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
        } else if (owner == null) {
            // Buy Logic
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

    /**
     * Handles the user interface logic for selecting a destination when landing on an Airplane square.
     *
     * @param player The player traveling by airplane.
     */
    public void handleAirplaneSquare(Player player) {
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

        handleAirplaneAction(player, targetPosition);
        handleLandedSquareAction(player);
    }

    /**
     * Executes the logic for moving a player via Airplane to a target position.
     * Calculates the steps required (considering board wrap-around) and moves the player.
     *
     * @param player         The player moving.
     * @param targetPosition The index of the destination square (0-19).
     */
    public void handleAirplaneAction(Player player, int targetPosition) {
        System.out.println("GUI: Flying " + player.getName() + " to " + board.getSquare(targetPosition).getName() + "...");

        int currentPos = player.getPosition();
        int boardSize = board.getBoardSize();

        int steps = targetPosition - currentPos;
        if (steps <= 0) {
            steps += boardSize;
        }

        player.move(steps);
    }

    public List<Player> getPlayers() { return players; }
    public Board getBoard() { return board; }
    public Dice getDice() { return dice; }
    public CardManager getCardManager() { return cardManager; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public Scanner getScanner() { return scanner; }
}