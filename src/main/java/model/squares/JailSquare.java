package model;

public class JailSquare extends Square {
    public JailSquare(String name, int position) {
        super(name, position);
    }

    @Override
    public void landOn(Player player) {
        // *** ลบ หรือ Comment บรรทัดนี้ออกครับ ไม่งั้นเดินตกเฉยๆ ก็จะติดคุก ***
        // player.setInJail(true);

        System.out.println(player.getName() + " is just visiting Jail.");
    }
}