package ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main entry point for the "Inverse Monopoly" (Beggar Game) application.
 * <p>
 * This class extends {@link Application} to set up the JavaFX primary stage
 * and launch the initial {@link GameMenu}.
 * </p>
 */
public class Main extends Application {

    /** The standard width of the application window. */
    public static final int WIDTH = 1000;
    /** The standard height of the application window. */
    public static final int HEIGHT = 800;

    /**
     * Starts the JavaFX application.
     * <p>
     * Sets up the primary stage title and scene, then displays the Game Menu.
     * </p>
     *
     * @param primaryStage The primary window for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        GameMenu menu = new GameMenu(primaryStage);

        primaryStage.setTitle("เกมยาจก");
        primaryStage.setScene(menu.createMenuScene());
        primaryStage.show();
    }

    /**
     * The main method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}