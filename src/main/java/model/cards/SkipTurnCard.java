package model.cards;

import model.*;

public class SkipTurnCard extends Card {
    private final int turnsToSkip;

    public SkipTurnCard(String description, int turnsToSkip) {
        super(description);
        this.turnsToSkip = turnsToSkip;
    }

    @Override
    public void execute(Player player, Board board) {
        System.out.println(">> Card Action: " + getDescription());
        player.setSkipTurns(player.getSkipTurns() + turnsToSkip);
        System.out.println(player.getName() + " must skip " + player.getSkipTurns() + " turn(s).");
    }
}