package model;

public class JailSquare extends Square {
    public JailSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        player.setInJail(true);
        System.out.println(player.getName() + " is just visiting Jail.");
    }
}