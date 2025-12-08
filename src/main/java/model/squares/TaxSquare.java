package model.squares;

import model.Player;

import java.util.Random;

public class TaxSquare extends Square {
    private final Random random;
    private int lastTaxPaid = 0; // ตัวแปรเก็บค่าภาษีล่าสุดที่สุ่มได้

    // ไม่รับ taxAmount ใน Constructor แล้ว
    public TaxSquare(String name, int position) {
        super(name, position);
        this.random = new Random();
    }

    @Override
    public void landOn(Player player) {
        // สุ่มภาษี: เช่น 100, 150, 200, 250, 300
        // (random.nextInt(5) ได้ 0-4) -> +2 ได้ 2-6 -> *50 ได้ 100-300
        int randomTax = (random.nextInt(5) + 2) * 50;

        this.lastTaxPaid = randomTax; // เก็บค่าไว้ให้ UI ดึงไปใช้

        System.out.println(player.getName() + " landed on " + getName());
        System.out.println(">> TAX PAYABLE: $" + lastTaxPaid);

        player.deductMoney(lastTaxPaid);
    }

    // เมธอดสำหรับให้ UI ดึงค่าไปแสดง
    public int getLastTaxPaid() {
        return lastTaxPaid;
    }
}