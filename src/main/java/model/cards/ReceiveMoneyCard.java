package model.cards;

import model.Board;
import model.Card;
import model.Player;

public class ReceiveMoneyCard extends Card {
    private final int amount;

    public ReceiveMoneyCard(String description, int amount) {
        super(description);
        this.amount = amount;
    }

    @Override
    public void execute(Player player, Board board) {
        System.out.println(">> Card Action: " + getDescription());
        player.addMoney(amount);
        System.out.println(player.getName() + " collected $" + amount);
    }
}