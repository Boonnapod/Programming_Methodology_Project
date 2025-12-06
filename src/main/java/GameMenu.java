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
    // กำหนดพาธสำหรับภาพปกเกม: bg.png
    private static final String COVER_IMAGE_PATH = "/Image/bg.png";

    public GameMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene createMenuScene() {
        // 1. โหลดภาพปกเกม (bg.png)
        ImageView coverImageView = null;
        try {
            Image coverImage = new Image(getClass().getResourceAsStream(COVER_IMAGE_PATH));
            coverImageView = new ImageView(coverImage);
            coverImageView.setFitWidth(Main.WIDTH * 0.9);
            coverImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพปก: " + COVER_IMAGE_PATH + " จะแสดงแค่ข้อความและปุ่มแทน");
        }

        Label titleLabel = new Label("เกมเศรษฐี");
        // ใช้ CSS Class แทนการกำหนด Font โดยตรง
        titleLabel.getStyleClass().add("title-label");

        Button startGameButton = new Button("เริ่มเกม");
        Button exitButton = new Button("ออกเกม");

        // ใช้ CSS Class สำหรับปุ่ม
        startGameButton.getStyleClass().add("menu-button");
        exitButton.getStyleClass().add("menu-button");

        // Event Handling
        startGameButton.setOnAction(e -> {
            CharacterSelectScreen selectScreen = new CharacterSelectScreen(primaryStage);
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
        menuLayout.getStyleClass().add("menu-background"); // ใช้ CSS background

        // 4. สร้าง Scene และเพิ่ม Stylesheet
        Scene menuScene = new Scene(menuLayout, Main.WIDTH, Main.HEIGHT);
        menuScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        return menuScene;
    }
}