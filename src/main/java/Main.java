import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // กำหนดขนาดหน้าจอหลัก
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        // สร้าง Object ของ GameMenu
        GameMenu menu = new GameMenu(primaryStage);

        primaryStage.setTitle("เกมเศรษฐี - เมนูหลัก");
        // กำหนด Scene เมนูให้เป็น Scene เริ่มต้น
        primaryStage.setScene(menu.createMenuScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}