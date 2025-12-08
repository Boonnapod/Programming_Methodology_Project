package model.squares;

import model.Player;

public class CardSquare extends Square {

    public CardSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        // Logic การจั่วการ์ดจะถูกจัดการใน GameController หลังจากเรียก landOn()
        System.out.println(player.getName() + " landed on " + getName() + ". Prepare to draw a card!");
    }
}