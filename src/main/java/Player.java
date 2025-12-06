public class Player {
    private int id;
    private int currentPosition;
    private int money;
    private String characterFile; // ชื่อไฟล์ตัวละครที่เลือก

    public Player(int id, String characterFile) {
        this.id = id;
        this.money = 1500; // เงินเริ่มต้น
        this.currentPosition = 0; // ตำแหน่งเริ่มต้น (ช่อง Go)
        this.characterFile = characterFile;
    }

    // Getters
    public int getId() { return id; }
    public int getCurrentPosition() { return currentPosition; }
    public int getMoney() { return money; }
    public String getCharacterFile() { return characterFile; }

    // Logic Methods
    public void addMoney(int amount) {
        this.money += amount;
    }

    public void deductMoney(int amount) {
        this.money -= amount;
    }

    // (สามารถเพิ่มเมธอด move, buyProperty, etc. ได้ในอนาคต)
}