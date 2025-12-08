package ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // กำหนดขนาดหน้าจอหลักให้ใหญ่ขึ้นเพื่อรองรับกระดาน 6x6
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        GameMenu menu = new GameMenu(primaryStage);

        primaryStage.setTitle("เกมเศรษฐี - เมนูหลัก");
        primaryStage.setScene(menu.createMenuScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        // ใช้ launch(args) เพื่อเรียก start() ใน JavaFX Application
        launch(args);
    }
}