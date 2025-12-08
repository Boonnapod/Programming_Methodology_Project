package ui;

import game.GameController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight; // *** NEW IMPORT ***
import javafx.stage.Stage;

public class GameMenu {

    private final Stage primaryStage;
    private static final String COVER_IMAGE_PATH = "/Image/bg.jpg";

    public GameMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene createMenuScene() {
        // 1. โหลดภาพปกเกม
        SoundManager.playBGM("main.mp3");

        ImageView coverImageView = null;
        try {
            Image coverImage = new Image(getClass().getResourceAsStream(COVER_IMAGE_PATH));
            coverImageView = new ImageView(coverImage);
            // ปรับขนาดภาพให้เล็กลงเล็กน้อย เพื่อให้พอดีกับหน้าจอเมื่อมีปุ่มอยู่ด้านล่าง
            coverImageView.setFitWidth(Main.WIDTH * 0.7);
            coverImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพปก: " + COVER_IMAGE_PATH);
        }

        // 2. UI Components
        Label titleLabel = new Label("เกมยาจก");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48)); // ใช้ FontWeight.BOLD
        titleLabel.setStyle("-fx-text-fill: #FFFFFF;"); // ตั้งค่าสีข้อความ

        Button startGameButton = new Button("เริ่มเกม");
        startGameButton.setMinWidth(250);
        startGameButton.setMinHeight(50);
        startGameButton.getStyleClass().add("menu-button"); // *** ADD STYLE CLASS ***

        Button exitButton = new Button("ออก");
        exitButton.setMinWidth(250);
        exitButton.setMinHeight(50);
        exitButton.getStyleClass().add("exit-button"); // *** ADD STYLE CLASS ***

        // Event Handling (สำคัญ: สร้าง GameController และส่งไป)
        startGameButton.setOnAction(e -> {
            // 1. สร้าง GameController ที่นี่
            GameController gameController = new GameController();

            // 2. ส่ง GameController ไปยัง CharacterSelectScreen
            // *** การเรียก CONSTRUCTOR ที่ถูกต้อง (แก้ไขแล้ว) ***
            CharacterSelectScreen selectScreen = new CharacterSelectScreen(
                    primaryStage,
                    gameController
            );

            primaryStage.setScene(selectScreen.createCharacterSelectScene());
            primaryStage.setTitle("เกมเศรษฐี - เลือกตัวละคร");
        });

        exitButton.setOnAction(e -> {
            Platform.exit();
        });

        // 3. Layout (VBox)
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(20));

        menuLayout.getChildren().add(titleLabel);
        if (coverImageView != null) {
            menuLayout.getChildren().add(coverImageView);
        }

        menuLayout.getChildren().addAll(startGameButton, exitButton);

        // 4. สร้าง Scene และใช้ Style
        menuLayout.getStyleClass().add("root-background"); // *** ADD BACKGROUND STYLE (สีเทา) ***

        Scene menuScene = new Scene(menuLayout, Main.WIDTH, Main.HEIGHT);
        menuScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        return menuScene;
    }
}