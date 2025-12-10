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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Represents the Main Menu screen of the game.
 * <p>
 * Displays the game title, cover image, and options to Start Game or Exit.
 * Initializes the {@link GameController} when a new game is started.
 * </p>
 */
public class GameMenu {

    private final Stage primaryStage;
    private static final String COVER_IMAGE_PATH = "/Image/bg.jpg";

    /**
     * Constructs the GameMenu.
     *
     * @param primaryStage The main stage of the application.
     */
    public GameMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Creates and returns the scene for the main menu.
     *
     * @return The {@link Scene} object containing the menu UI.
     */
    public Scene createMenuScene() {
        // 1. Start Menu BGM
        SoundManager.playBGM("main.mp3");

        ImageView coverImageView = null;
        try {
            Image coverImage = new Image(getClass().getResourceAsStream(COVER_IMAGE_PATH));
            coverImageView = new ImageView(coverImage);
            coverImageView.setFitWidth(Main.WIDTH * 0.7);
            coverImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: Cover image not found: " + COVER_IMAGE_PATH);
        }

        // 2. UI Components
        Label titleLabel = new Label("เกมยาจก");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setStyle("-fx-text-fill: #FFFFFF;");

        Button startGameButton = new Button("เริ่มเกม");
        startGameButton.setMinWidth(250);
        startGameButton.setMinHeight(50);
        startGameButton.getStyleClass().add("menu-button");

        Button exitButton = new Button("ออก");
        exitButton.setMinWidth(250);
        exitButton.setMinHeight(50);
        exitButton.getStyleClass().add("exit-button");

        // Event Handling
        startGameButton.setOnAction(e -> {
            // 1. Initialize GameController logic
            GameController gameController = new GameController();

            // 2. Transition to Character Selection Screen
            CharacterSelectScreen selectScreen = new CharacterSelectScreen(
                    primaryStage,
                    gameController
            );

            primaryStage.setScene(selectScreen.createCharacterSelectScene());
            primaryStage.setTitle("เกมยาจก");
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

        // 4. Styles
        menuLayout.getStyleClass().add("root-background");

        Scene menuScene = new Scene(menuLayout, Main.WIDTH, Main.HEIGHT);
        try {
            menuScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: style.css not found.");
        }

        return menuScene;
    }
}