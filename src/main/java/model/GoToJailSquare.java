package model;

public class GoToJailSquare extends Square {
    public GoToJailSquare(int position) {
        super("Go to Jail", position);
    }

    @Override
    public void landOn(Player player) {
        player.goToJail();
    }
}