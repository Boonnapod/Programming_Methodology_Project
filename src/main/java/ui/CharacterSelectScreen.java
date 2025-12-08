package ui;

import game.GameController;
import model.Player;
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

        // ปรับขนาดภาพตัวละครให้ใหญ่ขึ้นเล็กน้อย
        player1ImageView.setFitWidth(180);
        player1ImageView.setPreserveRatio(true);
        player2ImageView.setFitWidth(180);
        player2ImageView.setPreserveRatio(true);

        // โหลดภาพเริ่มต้น
        updateCharacterImage(player1ImageView, player1Index);
        updateCharacterImage(player2ImageView, player2Index);
    }

    public Scene createCharacterSelectScene() {
        SoundManager.playBGM("select.mp3");


        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-background"); // *** เพิ่มพื้นหลังสีเทา ***

        // 1. Title
        Label titleLabel = new Label("เลือกตัวละคร");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48)); // ใหญ่และหนาขึ้น
        titleLabel.setStyle("-fx-text-fill: #333333;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        root.setTop(titleLabel);

        // 2. Player Selection HBox
        VBox player1VBox = createCharacterSelectionPanel(true);
        VBox player2VBox = createCharacterSelectionPanel(false);

        HBox selectionHBox = new HBox(80, player1VBox, player2VBox); // เพิ่มระยะห่าง
        selectionHBox.setAlignment(Pos.CENTER);
        root.setCenter(selectionHBox);

        // 3. ปุ่มควบคุมด้านล่าง (เริ่มเกม & กลับ)
        Button startGameButton = new Button("เริ่มเกม");
        startGameButton.getStyleClass().add("menu-button"); // ใช้ Style ปุ่มเมนู
        startGameButton.setPrefWidth(250);

        Button backButton = new Button("กลับหน้าเมนู");
        backButton.getStyleClass().add("exit-button");
        backButton.setPrefWidth(200);

        HBox bottomControl = new HBox(50, backButton, startGameButton);
        bottomControl.setAlignment(Pos.CENTER);
        BorderPane.setMargin(bottomControl, new Insets(40, 0, 20, 0));
        root.setBottom(bottomControl);

        // Event Handling สำหรับปุ่มเริ่มเกม
        startGameButton.setOnAction(e -> {
            // 1. ดึง List ผู้เล่นจาก GameController (สมมติว่า GameController ได้สร้าง Players ไว้แล้ว)
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

        // Event Handling สำหรับปุ่มกลับหน้าเมนู
        backButton.setOnAction(e -> {
            primaryStage.setScene(new GameMenu(primaryStage).createMenuScene());
            primaryStage.setTitle("เกมเศรษฐี");
        });

        Scene selectScene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        // ต้องเรียกใช้ Style Sheet
        selectScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        return selectScene;
    }

    private VBox createCharacterSelectionPanel(boolean isPlayer1) {
        // Label แสดงชื่อผู้เล่น
        Label playerLabel = new Label(isPlayer1 ? "ผู้เล่น 1" : "ผู้เล่น 2");
        playerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        // ใช้สีแยกผู้เล่น
        String playerColor = isPlayer1 ? "#2E8B57" : "#FF6347";
        playerLabel.setStyle("-fx-text-fill: " + playerColor + ";");

        // ImageView (อ้างอิงจากตัวแปรสมาชิก)
        ImageView imageView = isPlayer1 ? player1ImageView : player2ImageView;

        // ปุ่มควบคุม (HBox)
        Button prevButton = new Button("◄");
        Button nextButton = new Button("►");

        // *** Style สำหรับปุ่มควบคุม (วงกลม) ***
        String controlButtonStyle = "-fx-min-width: 50px; -fx-min-height: 50px; -fx-max-width: 50px; -fx-max-height: 50px; " +
                "-fx-background-radius: 25; -fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-background-color: #555555; -fx-text-fill: white; -fx-cursor: hand;";
        prevButton.setStyle(controlButtonStyle);
        nextButton.setStyle(controlButtonStyle);

        // เพิ่ม hover effect เล็กน้อย (optional)
        prevButton.setOnMouseEntered(e -> prevButton.setStyle(controlButtonStyle + "-fx-background-color: #777777;"));
        prevButton.setOnMouseExited(e -> prevButton.setStyle(controlButtonStyle));
        nextButton.setOnMouseEntered(e -> nextButton.setStyle(controlButtonStyle + "-fx-background-color: #777777;"));
        nextButton.setOnMouseExited(e -> nextButton.setStyle(controlButtonStyle));

        prevButton.setOnAction(e -> changeCharacter(isPlayer1, -1));
        nextButton.setOnAction(e -> changeCharacter(isPlayer1, 1));

        HBox controlBox = new HBox(30, prevButton, nextButton);
        controlBox.setAlignment(Pos.CENTER);

        // VBox รวมทั้งหมด (Panel)
        VBox panel = new VBox(30, playerLabel, imageView, controlBox); // เพิ่มระยะห่าง
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(30));
        panel.getStyleClass().add("character-box"); // *** ใช้ Style Box ***

        // เพิ่มเส้นขอบตามสีผู้เล่น
        panel.setStyle("-fx-border-color: " + playerColor + "; -fx-border-width: 4; -fx-border-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0.0, 0, 0);");

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