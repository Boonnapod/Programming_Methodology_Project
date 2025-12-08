package ui;

import game.GameController;
import model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CharacterSelectScreen {

    private final Stage primaryStage;
    private final GameController gameController;

    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png","character_04.png", "character_05.png", "character_06.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MAX_CHARACTERS = CHARACTER_FILES.length;

    private int player1Index = 0;
    private int player2Index = 0;

    private final ImageView player1ImageView = new ImageView();
    private final ImageView player2ImageView = new ImageView();

    public CharacterSelectScreen(Stage primaryStage, GameController gameController) {
        this.primaryStage = primaryStage;
        this.gameController = gameController;
    }

    public Scene createCharacterSelectScene() {
        SoundManager.playBGM("select.mp3");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-background");

        // โหลดรูปภาพเริ่มต้น
        updateCharacterImage(player1ImageView, player1Index);
        updateCharacterImage(player2ImageView, player2Index);

        // กล่องเลือกตัวละคร
        VBox p1Box = createPlayerSelectionBox("Player 1", player1ImageView, true);
        VBox p2Box = createPlayerSelectionBox("Player 2", player2ImageView, false);

        HBox centerBox = new HBox(50, p1Box, p2Box);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        // Header ด้านบน
        Label headerLabel = new Label("CHOOSE YOUR CHARACTER");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        headerLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 3, 0, 0, 2);");

        VBox topBox = new VBox(headerLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(40, 0, 0, 0));
        root.setTop(topBox);

        // --- ส่วนปุ่มควบคุมด้านล่าง (แก้ไขใหม่) ---

        // 1. ปุ่ม Start Game
        Button startButton = new Button("START GAME");
        startButton.getStyleClass().add("menu-button");
        startButton.setPrefWidth(200);
        startButton.setOnAction(e -> {
            List<Integer> selectedIndices = new ArrayList<>();
            selectedIndices.add(player1Index);
            selectedIndices.add(player2Index);

            List<Player> players = gameController.getPlayers();

            GameBoardScreen gameBoard = new GameBoardScreen(
                    primaryStage,
                    gameController,
                    players,
                    selectedIndices
            );

            primaryStage.setScene(gameBoard.createGameScene());
        });

        // 2. ปุ่ม Back to Menu (เพิ่มใหม่)
        Button backButton = new Button("BACK TO MENU");
        backButton.getStyleClass().add("menu-button");
        backButton.setPrefWidth(200);
        // ปรับสีปุ่ม Back ให้เป็นสีเทาๆ เพื่อให้ Start Game เด่นกว่า
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");

        backButton.setOnAction(e -> {
            // กลับไปหน้าเมนู
            GameMenu gameMenu = new GameMenu(primaryStage);
            primaryStage.setScene(gameMenu.createMenuScene());
        });

        // 3. จัดวางลง VBox
        VBox bottomBox = new VBox(20, startButton, backButton); // เพิ่มระยะห่างระหว่างปุ่มเป็น 20
        bottomBox.setAlignment(Pos.CENTER);

        // เพิ่ม Padding ด้านบน (ตัวเลขแรก) เป็น 60 เพื่อดันให้ห่างจากรูปตัวละคร
        bottomBox.setPadding(new Insets(60, 0, 40, 0));

        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 800);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: style.css not found!");
        }

        return scene;
    }

    private VBox createPlayerSelectionBox(String titleText, ImageView imageView, boolean isPlayer1) {
        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, black, 2, 0, 1, 1);");

        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setEffect(new DropShadow(10, Color.BLACK));

        Button prevButton = new Button("<");
        prevButton.setStyle("-fx-font-size: 20px; -fx-min-width: 50px; -fx-background-radius: 100; -fx-cursor: hand;");
        prevButton.setOnAction(e -> changeCharacter(isPlayer1, -1));

        Button nextButton = new Button(">");
        nextButton.setStyle("-fx-font-size: 20px; -fx-min-width: 50px; -fx-background-radius: 100; -fx-cursor: hand;");
        nextButton.setOnAction(e -> changeCharacter(isPlayer1, 1));

        HBox selectorBox = new HBox(20, prevButton, imageView, nextButton);
        selectorBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(15, title, selectorBox);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        String borderColor = isPlayer1 ? "#2ecc71" : "#e74c3c";
        container.setStyle(
                "-fx-background-color: rgba(0,0,0,0.3);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 20;"
        );

        return container;
    }

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

    private void updateCharacterImage(ImageView imageView, int index) {
        try {
            String fileName = CHARACTER_FILES[index];
            imageView.setImage(new Image(getClass().getResourceAsStream(RESOURCE_PATH + fileName)));
        } catch (Exception e) {
            System.err.println("Error loading character image: " + CHARACTER_FILES[index]);
        }
    }
}