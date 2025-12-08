package model.squares;

import model.Player;

public class AirplaneSquare extends Square {

    public AirplaneSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        // Logic การเลือกปลายทางจะถูกจัดการใน GameController
        System.out.println(player.getName() + " landed on " + getName() + ". Choose your destination!");
    }
}