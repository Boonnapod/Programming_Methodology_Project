package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Manages the playback of Background Music (BGM) for the application.
 * <p>
 * This utility class uses a static {@link MediaPlayer} to ensure only one BGM track plays at a time.
 * It handles loading audio files from resources, looping them, and managing volume.
 * </p>
 */
public class SoundManager {

    // Holds the current media player instance (static to be accessible globally)
    private static MediaPlayer currentBGM;

    /**
     * Plays a new BGM track, automatically stopping any currently playing track.
     *
     * @param fileName The name of the audio file (including extension) located in the {@code /soundfx/} resource folder.
     */
    public static void playBGM(String fileName) {
        try {
            // 1. Stop existing music if playing
            if (currentBGM != null) {
                currentBGM.stop();
                currentBGM.dispose(); // Release resources
            }

            // 2. Load the new sound file
            URL resource = SoundManager.class.getResource("/soundfx/" + fileName);
            if (resource == null) {
                System.err.println("!!! BGM not found: /soundfx/" + fileName);
                return;
            }

            // 3. Create MediaPlayer
            Media media = new Media(resource.toExternalForm());
            currentBGM = new MediaPlayer(media);

            // 4. Set to loop indefinitely
            currentBGM.setCycleCount(MediaPlayer.INDEFINITE);

            // 5. Set volume (0.0 - 1.0)
            currentBGM.setVolume(0.5);

            // 6. Start playing
            currentBGM.play();
            System.out.println(">> Playing BGM: " + fileName);

        } catch (Exception e) {
            System.err.println("Error playing BGM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stops the currently playing BGM.
     * Useful when the game ends or when exiting the application.
     */
    public static void stopBGM() {
        if (currentBGM != null) {
            currentBGM.stop();
        }
    }
}