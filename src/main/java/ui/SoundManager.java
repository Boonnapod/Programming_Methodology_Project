package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    // ตัวแปรเก็บ MediaPlayer ตัวปัจจุบัน (static เพื่อให้เข้าถึงได้จากทุกหน้า)
    private static MediaPlayer currentBGM;

    /**
     * สั่งเล่นเพลง BGM ใหม่ (เพลงเก่าจะหยุดอัตโนมัติ)
     * @param fileName ชื่อไฟล์ในโฟลเดอร์ /soundfx/
     */
    public static void playBGM(String fileName) {
        try {
            // 1. ถ้ามีเพลงเก่าเล่นอยู่ ให้หยุดก่อน
            if (currentBGM != null) {
                currentBGM.stop();
                currentBGM.dispose(); // คืน resource
            }

            // 2. โหลดไฟล์เสียงใหม่
            URL resource = SoundManager.class.getResource("/soundfx/" + fileName);
            if (resource == null) {
                System.err.println("!!! ไม่พบไฟล์ BGM: /soundfx/" + fileName);
                return;
            }

            // 3. สร้าง MediaPlayer
            Media media = new Media(resource.toExternalForm());
            currentBGM = new MediaPlayer(media);

            // 4. ตั้งค่าให้วนลูป (Loop Indefinitely)
            currentBGM.setCycleCount(MediaPlayer.INDEFINITE);

            // 5. ปรับความดัง (0.0 - 1.0) ปรับได้ตามชอบ
            currentBGM.setVolume(0.5);

            // 6. เล่นเพลง
            currentBGM.play();
            System.out.println(">> Playing BGM: " + fileName);

        } catch (Exception e) {
            System.err.println("Error playing BGM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * สั่งหยุดเพลง (ใช้ตอนจบเกม หรือปิดโปรแกรม)
     */
    public static void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
        }
    }
}