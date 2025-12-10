package model.cards;

import model.Board;
import model.Player;

/**
 * A card that forces the player to move to the Airplane Square (Airport).
 */
public class MoveToAirPlane extends Card {

    // Target position for the Airport (index 15)
    private static final int AIRPLANE_POSITION = 15;

    /**
     * Constructs a MoveToAirPlane card.
     *
     * @param description The text describing the action.
     */
    public MoveToAirPlane(String description) {
        super(description);
    }

    /**
     * Teleports the player to the Airplane position.
     * <p>
     * Instead of setting the position directly, this method calculates the number of steps
     * required to reach the target index moving forward. This ensures that if the player
     * passes the "Go" square (index 0) during the travel, the game logic (in {@code Player.move()})
     * will correctly trigger the "Pass Go" reward.
     * </p>
     *
     * @param currentPlayer The player who drew the card.
     * @param board         The game board.
     */
    @Override
    public void execute(Player currentPlayer, Board board) {

        System.out.println(">> Card Action: " + getDescription());
        System.out.println(">> " + currentPlayer.getName() + " is teleported to the Airport (Position " + AIRPLANE_POSITION + ")!");

        int currentPos = currentPlayer.getPosition();
        // Assumes board size is 20
        int boardSize = 20;

        // Calculate steps needed to reach AIRPLANE_POSITION moving forward
        int steps;
        if (currentPos <= AIRPLANE_POSITION) {
            steps = AIRPLANE_POSITION - currentPos;
        } else {
            // Wrap around the board (e.g., from 18 to 15)
            steps = boardSize - currentPos + AIRPLANE_POSITION;
        }

        // Move the player using standard movement logic to handle "Pass Go" events
        currentPlayer.move(steps);

        System.out.println(">> " + currentPlayer.getName() + " is now at position " + currentPlayer.getPosition() + " (" + board.getSquare(currentPlayer.getPosition()).getName() + ").");

        // Note: The logic for the new square (AirplaneSquare) will be handled by the GameController
    }
}