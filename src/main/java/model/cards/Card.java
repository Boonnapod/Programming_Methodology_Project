package model.cards;

import model.Board;
import model.Player;

/**
 * คลาสแม่ (Abstract) สำหรับการ์ดทุกประเภท
 * กำหนดโครงสร้างพื้นฐานและเมธอด execute() ที่เป็น abstract
 */
public abstract class Card {
    private final String description;

    public Card(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * เมธอดสำหรับดำเนินการของการ์ด ซึ่งคลาสลูกต้อง implement (Polymorphism)
     * @param player ผู้เล่นที่จั่วการ์ด
     * @param board กระดานเกม (ใช้สำหรับหาตำแหน่งหรือทรัพย์สิน)
     */
    public abstract void execute(Player player, Board board);
}