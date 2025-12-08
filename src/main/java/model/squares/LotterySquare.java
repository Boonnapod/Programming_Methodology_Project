package model;

import java.util.Random;

public class LotterySquare extends Square {
    private final Random random;
    private int lastPrize = 0;

    public LotterySquare(String name, int position) {
        super(name, position);
        this.random = new Random();
    }

    @Override
    public void landOn(Player player) {
        // สุ่มเลข 1-5 แล้วคูณ 100
        int luckyNumber = random.nextInt(5) + 1;
        this.lastPrize = luckyNumber * 100;

        System.out.println(player.getName() + " landed on " + getName());
        System.out.println(">> LOTTERY RESULT: You win $" + lastPrize);

        player.addMoney(lastPrize);
    }

    public int getLastPrize() {
        return lastPrize;
    }
}