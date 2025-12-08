package model;

public class GoSquare extends Square {
    public GoSquare(int position) {
        super("Go", position);
    }

    @Override
    public void landOn(Player player) {
        // โลจิกการรับเงินเมื่อถึง Go ถูกจัดการแล้วใน Player.move()
        System.out.println(player.getName() + " landed on Go.");
    }
}