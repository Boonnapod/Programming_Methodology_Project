package ui;

import game.GameController; // *** NEW IMPORT ***
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
import javafx.stage.Stage;

public class GameMenu {

    private final Stage primaryStage;
    private static final String COVER_IMAGE_PATH = "/Image/bg.jpg";

    public GameMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene createMenuScene() {
        // 1. โหลดภาพปกเกม
        ImageView coverImageView = null;
        try {
            Image coverImage = new Image(getClass().getResourceAsStream(COVER_IMAGE_PATH));
            coverImageView = new ImageView(coverImage);
            coverImageView.setFitWidth(Main.WIDTH * 0.9);
            coverImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพปก: " + COVER_IMAGE_PATH);
        }

        // 2. UI Components
        Label titleLabel = new Label("เกมเศรษฐี");
        titleLabel.setFont(new Font("Arial", 48));

        Button startGameButton = new Button("เริ่มเกม");
        startGameButton.setMinWidth(200);
        startGameButton.setMinHeight(40);

        Button exitButton = new Button("ออกเกม");
        exitButton.setMinWidth(200);
        exitButton.setMinHeight(40);

        // Event Handling (สำคัญ: สร้าง GameController และส่งไป)
        startGameButton.setOnAction(e -> {
            // *** 1. สร้าง GameController ที่นี่ ***
            GameController gameController = new GameController();

            // *** 2. ส่ง GameController ไปยัง CharacterSelectScreen ***
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

        if (coverImageView != null) {
            menuLayout.getChildren().add(coverImageView);
        } else {
            menuLayout.getChildren().add(titleLabel);
        }

        menuLayout.getChildren().addAll(startGameButton, exitButton);

        // 4. สร้าง Scene
        Scene menuScene = new Scene(menuLayout, Main.WIDTH, Main.HEIGHT);
        return menuScene;
    }
}