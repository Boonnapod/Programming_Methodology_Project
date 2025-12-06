package ui; // *** 1. เพิ่ม Package กลับมา ***

import game.GameController; // *** 2. แก้ไข Import ให้สมบูรณ์ ***
import model.Board; // แก้ไข Import
import model.Player; // แก้ไข Import
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import ui.Main;

import java.util.ArrayList;
import java.util.List;

public class CharacterSelectScreen {

    private final Stage primaryStage;
    private final GameController gameController;

    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MAX_CHARACTERS = CHARACTER_FILES.length;

    private int player1Index = 0;
    private int player2Index = 0;

    private ImageView player1ImageView = new ImageView();
    private ImageView player2ImageView = new ImageView();

    public CharacterSelectScreen(Stage primaryStage, GameController gameController) {
        this.primaryStage = primaryStage;
        this.gameController = gameController;

        player1ImageView.setFitWidth(150);
        player1ImageView.setPreserveRatio(true);
        player2ImageView.setFitWidth(150);
        player2ImageView.setPreserveRatio(true);

        updateCharacterImage(player1ImageView, player1Index);
        updateCharacterImage(player2ImageView, player2Index);
    }

    public Scene createCharacterSelectScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // 1. Title
        Label titleLabel = new Label("เลือกตัวละคร");
        titleLabel.setFont(Font.font("Arial", 36));
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // 2. Player 1 Selection
        VBox player1VBox = createCharacterSelectionPanel(true);

        // 3. Player 2 Selection
        VBox player2VBox = createCharacterSelectionPanel(false);

        HBox selectionHBox = new HBox(50, player1VBox, player2VBox);
        selectionHBox.setAlignment(Pos.CENTER);
        root.setCenter(selectionHBox);

        // 4. ปุ่มเริ่มเกม
        Button startGameButton = new Button("เริ่มเกม");
        startGameButton.setFont(Font.font("Arial", 24));
        startGameButton.setPadding(new Insets(10, 30, 10, 30));
        BorderPane.setAlignment(startGameButton, Pos.CENTER);
        BorderPane.setMargin(startGameButton, new Insets(20, 0, 0, 0));
        root.setBottom(startGameButton);

        // Event Handling สำหรับเริ่มเกม
        startGameButton.setOnAction(e -> {

            // 1. ดึง List ผู้เล่นจาก GameController
            List<Player> selectedPlayers = gameController.getPlayers();

            // 2. สร้าง List ของ Index ตัวละครที่ถูกเลือก
            List<Integer> selectedCharIndices = new ArrayList<>();
            selectedCharIndices.add(player1Index);
            selectedCharIndices.add(player2Index);

            // 3. สร้างหน้าจอเกมใหม่
            GameBoardScreen gameScreen = new GameBoardScreen(
                    primaryStage,
                    gameController,
                    selectedPlayers,
                    selectedCharIndices
            );

            primaryStage.setScene(gameScreen.createGameScene());
            primaryStage.setTitle("เกมเศรษฐี - กำลังเล่น");
        });

        return new Scene(root, Main.WIDTH, Main.HEIGHT);
    }

    private VBox createCharacterSelectionPanel(boolean isPlayer1) {
        // Label แสดงชื่อผู้เล่น
        Label playerLabel = new Label(isPlayer1 ? "ผู้เล่น 1" : "ผู้เล่น 2");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // ImageView (อ้างอิงจากตัวแปรสมาชิก)
        ImageView imageView = isPlayer1 ? player1ImageView : player2ImageView;

        // ปุ่มควบคุม (HBox)
        Button prevButton = new Button("<");
        prevButton.setFont(Font.font("Arial", 18));
        Button nextButton = new Button(">");
        nextButton.setFont(Font.font("Arial", 18));

        prevButton.setOnAction(e -> changeCharacter(isPlayer1, -1));
        nextButton.setOnAction(e -> changeCharacter(isPlayer1, 1));

        HBox controlBox = new HBox(20, prevButton, nextButton);
        controlBox.setAlignment(Pos.CENTER);

        // VBox รวมทั้งหมด
        VBox panel = new VBox(20, playerLabel, imageView, controlBox);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));
        return panel;
    }

    /**
     * เมธอดสำหรับเปลี่ยน Index และอัปเดตภาพ
     */
    private void changeCharacter(boolean isPlayer1, int direction) {
        int currentIndex = isPlayer1 ? player1Index : player2Index;

        int newIndex = (currentIndex + direction) % MAX_CHARACTERS;

        if (newIndex < 0) {
            newIndex = MAX_CHARACTERS - 1;
        }

        if (isPlayer1) {
            player1Index = newIndex;
            updateCharacterImage(player1ImageView, player1Index);
        } else {
            player2Index = newIndex;
            updateCharacterImage(player2ImageView, player2Index);
        }
    }

    /**
     * เมธอดสำหรับโหลดและตั้งค่า Image ให้กับ ImageView
     */
    private void updateCharacterImage(ImageView imageView, int index) {
        try {
            String fileName = CHARACTER_FILES[index];
            String fullPath = RESOURCE_PATH + fileName;

            Image image = new Image(getClass().getResourceAsStream(fullPath));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพตัวละคร: " + CHARACTER_FILES[index] + " - ใช้ภาพสำรองแทน");
        }
    }
}