package model;

public interface Rentable {
    /**
     * คำนวณค่าเช่าที่ผู้เล่นต้องจ่าย
     * @return จำนวนเงินค่าเช่า
     */
    int calculateRent(int diceRoll);
}