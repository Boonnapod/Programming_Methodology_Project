package model;

/**
 * Represents a player in the game.
 * <p>
 * This class maintains the player's state, including current money, board position,
 * jail status, and any skip-turn penalties. It also handles movement logic and financial transactions.
 * </p>
 */
public class Player {
    private final String name;
    private int money;
    private int position; // 0 to 19
    private boolean inJail;
    private int skipTurns;

    // Countdown counter for jail sentence
    private int jailTurns = 0;

    private static final int SMALL_BOARD_SIZE = 20;
    private static final int INITIAL_MONEY_DEFAULT = 1000;

    /**
     * Constructs a new Player with a starting name.
     * <p>
     * Initializes money to the default amount ($1000), position to Start (0),
     * and clears any penalty statuses.
     * </p>
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.money = INITIAL_MONEY_DEFAULT;
        this.position = 0;
        this.inJail = false;
        this.skipTurns = 0;
        this.jailTurns = 0;
    }

    // --- Getters ---
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isInJail() { return inJail; }
    public int getSkipTurns() { return skipTurns; }
    public static int getInitialMoney() { return INITIAL_MONEY_DEFAULT; }

    /**
     * Gets the remaining number of turns the player must stay in jail.
     *
     * @return The number of turns remaining.
     */
    public int getJailTurns() { return jailTurns; }

    // --- Setters / Actions ---

    /**
     * Sets the number of turns a player must skip.
     *
     * @param skipTurns The number of turns to skip.
     */
    public void setSkipTurns(int skipTurns) { this.skipTurns = skipTurns; }

    /**
     * Adds money to the player's balance.
     *
     * @param amount The amount to add.
     */
    public void addMoney(int amount) {
        this.money += amount;
        System.out.println("💵 " + this.name + " received $" + amount + ". New Balance: $" + this.money);
    }

    /**
     * Deducts money from the player's balance.
     * <p>
     * If the player has enough funds, the amount is deducted and the method returns {@code true}.
     * If funds are insufficient, the money is still deducted (resulting in a negative balance or bankruptcy),
     * and the method returns {@code false}.
     * </p>
     *
     * @param amount The amount to pay.
     * @return {@code true} if payment was successful with positive balance; {@code false} otherwise.
     */
    public boolean deductMoney(int amount) {
        if (this.money >= amount) {
            this.money -= amount;
            System.out.println("💸 " + this.name + " paid $" + amount + ". New Balance: $" + this.money);
            return true;
        } else {
            // Bankruptcy logic trigger
            this.money -= amount;
            return false;
        }
    }

    /**
     * Sends the player to Jail.
     * <p>
     * Moves the player to the Jail square (index 5), sets the {@code inJail} flag to true,
     * and sets the {@code jailTurns} penalty to 2.
     * </p>
     */
    public void goToJail() {
        this.inJail = true;
        this.position = 5; // Jail Position
        this.jailTurns = 2; // Default sentence
        System.out.println("🚨 " + name + " is sent to Jail! (Stuck for 2 turns)");
    }

    /**
     * Releases the player from Jail immediately.
     * Resets jail status and jail turn counter.
     */
    public void releaseFromJail() {
        this.inJail = false;
        this.jailTurns = 0;
        System.out.println("🔓 " + name + " is released from Jail.");
    }

    /**
     * Decrements the jail sentence counter.
     * Typically called when a player chooses to wait out their sentence.
     */
    public void decrementJailTurns() {
        if (this.jailTurns > 0) {
            this.jailTurns--;
        }
    }

    /**
     * Moves the player forward by a specified number of steps.
     * <p>
     * Handles wrapping around the board (modulo arithmetic) and checks for the "Pass Go" event.
     * If the player passes Go, they receive $20.
     * </p>
     *
     * @param steps The number of steps to move.
     */
    public void move(int steps) {
        int oldPosition = this.position;
        this.position = (oldPosition + steps) % SMALL_BOARD_SIZE;

        // Check for passing Go (Index 0)
        if (oldPosition + steps >= SMALL_BOARD_SIZE) {
            addMoney(100);
            System.out.println("🎉 " + name + " passed Go and collected $100.");
        }
    }

    /**
     * Teleports the player to a specific position without triggering movement events (like passing Go).
     *
     * @param position The target index on the board.
     */
    public void warp(int position) {
        this.position = position;
    }

    /**
     * Sets the player's position directly.
     *
     * @param position The new position index.
     */
    public void setPosition(int position) {
        this.position = position;
    }
}