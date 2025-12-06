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
import javafx.stage.Stage;

public class CharacterSelectScreen {

    private final Stage primaryStage;

    // อาร์เรย์ของไฟล์ตัวละคร (3 ตัว)
    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MAX_CHARACTERS = CHARACTER_FILES.length;

    private int player1Index = 0;
    private int player2Index = 0;

    private ImageView player1ImageView = new ImageView();
    private ImageView player2ImageView = new ImageView();

    public CharacterSelectScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;

        player1ImageView.setFitWidth(150);
        player1ImageView.setFitHeight(150);
        player2ImageView.setFitWidth(150);
        player2ImageView.setFitHeight(150);

        updateCharacterImage(player1ImageView, player1Index);
        updateCharacterImage(player2ImageView, player2Index);
    }

    public Scene createCharacterSelectScene() {
        Label titleLabel = new Label("เลือกตัวละครสำหรับผู้เล่น 2 คน");
        titleLabel.getStyleClass().add("subtitle-label");

        VBox topBox = new VBox(10, titleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));

        Button confirmButton = new Button("ยืนยันและเริ่มเกม");
        confirmButton.getStyleClass().add("confirm-button");

        HBox bottomBox = new HBox(confirmButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        VBox player1Selection = createPlayerSelectionBox("ผู้เล่น 1", player1ImageView, true);
        VBox player2Selection = createPlayerSelectionBox("ผู้เล่น 2", player2ImageView, false);

        HBox centerHBox = new HBox(50, player1Selection, player2Selection);
        centerHBox.setAlignment(Pos.CENTER);
        centerHBox.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centerHBox);
        root.setBottom(bottomBox);
        root.getStyleClass().add("select-background");

        // Event Handling: สร้าง Object ผู้เล่นเมื่อยืนยัน
        confirmButton.setOnAction(e -> {
            String p1CharFile = CHARACTER_FILES[player1Index];
            String p2CharFile = CHARACTER_FILES[player2Index];

            // สร้าง Object ผู้เล่น
            Player player1 = new Player(1, p1CharFile);
            Player player2 = new Player(2, p2CharFile);

            System.out.println("เริ่มเกม! P1 (" + player1.getCharacterFile() + "), P2 (" + player2.getCharacterFile() + ")");

            // TODO: เชื่อมต่อกับ Game Logic และโหลดหน้ากระดานเกม (Board)
            primaryStage.setTitle("เกมเศรษฐี - กำลังเล่น");
        });

        // 6. สร้าง Scene และเพิ่ม Stylesheet
        Scene selectScene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        selectScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        return selectScene;
    }

    private VBox createPlayerSelectionBox(String title, ImageView imageView, boolean isPlayer1) {
        Label playerTitle = new Label(title);
        playerTitle.getStyleClass().add("player-title");

        Button prevButton = new Button("<");
        Button nextButton = new Button(">");

        prevButton.getStyleClass().add("nav-button");
        nextButton.getStyleClass().add("nav-button");

        HBox controlBox = new HBox(15, prevButton, imageView, nextButton);
        controlBox.setAlignment(Pos.CENTER);

        VBox selectionBox = new VBox(15, playerTitle, controlBox);
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setPadding(new Insets(20));
        selectionBox.getStyleClass().add("selection-box");

        prevButton.setOnAction(e -> changeCharacter(isPlayer1, -1));
        nextButton.setOnAction(e -> changeCharacter(isPlayer1, 1));

        return selectionBox;
    }

    private void changeCharacter(boolean isPlayer1, int direction) {
        int currentIndex = isPlayer1 ? player1Index : player2Index;
        int newIndex = (currentIndex + direction) % MAX_CHARACTERS;

        if (newIndex < 0) { newIndex = MAX_CHARACTERS - 1; }

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
            String fullPath = RESOURCE_PATH + CHARACTER_FILES[index];
            Image image = new Image(getClass().getResourceAsStream(fullPath));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพ: " + CHARACTER_FILES[index] + ". ตรวจสอบพาธ!");
            imageView.setImage(null);
        }
    }
}