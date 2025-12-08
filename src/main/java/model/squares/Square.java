package model.squares;

import model.Player;

public abstract class Square {
    private final String name;
    private final int position;

    public Square(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    /**
     * Method ที่ทุกช่องต้อง Implement (Polymorphism)
     */
    public abstract void landOn(Player player);
}