package model.cards;

import model.Board;
import model.Player;

/**
 * คลาสแม่ (Abstract) สำหรับการ์ดทุกประเภท
 * - กำหนดโครงสร้างข้อมูลพื้นฐาน (description)
 * - implement สัญญา CardAction
 */
public abstract class Card { // *** เพิ่ม implements CardAction ***
    private final String description;

    public Card(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * เมธอด execute() ถูกบังคับให้ต้องมีโดย CardAction
     * เรายังคงใช้ abstract เพื่อบังคับให้คลาสลูกต้อง implement
     * (ถึงแม้ว่า Interface จะบังคับอยู่แล้ว แต่ทำให้โครงสร้างดูชัดเจนขึ้น)
     */
    public abstract void execute(Player player, Board board);
}