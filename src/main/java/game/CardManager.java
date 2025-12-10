package game;

import model.cards.Card;
import model.Player;
import model.cards.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages the deck of cards in the game.
 * <p>
 * This class handles initialization, shuffling, and drawing of cards.
 * It ensures that the deck never runs out by recycling drawn cards to the bottom of the deck.
 * </p>
 */
public class CardManager {
    private final List<Card> deck;

    /**
     * Constructs a new CardManager and initializes the deck with a set of predefined cards.
     *
     * @param players The list of players in the game (used for initializing specific card logic if needed).
     */
    public CardManager(List<Player> players) {
        this.deck = new ArrayList<>();
        initializeDeck(players);
        Collections.shuffle(deck);
    }

    /**
     * Populates the deck with a predefined set of action cards.
     * <p>
     * This method initializes the deck by adding various card types, including:
     * <ul>
     * <li><b>MoveToAirPlane:</b> Teleports the player to the Airport square.</li>
     * <li><b>SkipTurnCard:</b> Forces the player to miss their next turn.</li>
     * <li><b>ReceiveMoneyCard:</b> Grants a cash reward. One card features a randomized reward
     * calculated as 10-20% of the player's initial money.</li>
     * </ul>
     * Duplicate cards are also added to ensure the deck has sufficient volume for gameplay.
     * </p>
     *
     * @param players The list of players in the game (currently unused in this specific logic, but available for future scaling).
     */
    private void initializeDeck(List<Player> players) {
        // 3.1 Move to Airplane card
        deck.add(new MoveToAirPlane("Move to AirPlane"));

        // 3.2 Skip Turn card
        deck.add(new SkipTurnCard("You've missed your train. Skip your next turn.", 1));

        // 3.3 Receive Money card (Random 10-20%)
        Random random = new Random();
        int amount = Player.getInitialMoney() * (random.nextInt(11) + 10) / 100; // 10% - 20%
        deck.add(new ReceiveMoneyCard("You won the lottery! Collect $" + amount, amount));

        // Add duplicate cards to increase deck size
        deck.add(new SkipTurnCard("The taxman caught you. Skip next turn.", 1));
        deck.add(new ReceiveMoneyCard("OraOraHaHa! Collect $150.", 150));
        deck.add(new MoveToAirPlane("You can fly."));
    }

    /**
     * Draws the top card from the deck.
     * <p>
     * The drawn card is removed from the top and immediately added back to the bottom of the deck,
     * creating an infinite loop of cards.
     * </p>
     *
     * @return The {@link Card} drawn from the top of the deck.
     */
    public Card drawCard() {
        // Draw card from the top
        Card drawnCard = deck.remove(0);

        // Recycle the card to the bottom of the deck
        deck.add(drawnCard);

        return drawnCard;
    }
}