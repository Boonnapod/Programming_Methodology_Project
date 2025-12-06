package ui; // เปลี่ยนเป็น Package ui

import game.GameController; // เปลี่ยน Import
import model.Board; // เปลี่ยน Import
import model.Player; // เปลี่ยน Import
import model.Square; // เปลี่ยน Import
// Imports for JavaFX UI
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.ArrayList;

// (สมมติว่า Main.java อยู่ใน Package ที่ไม่มีชื่อ หรือมี Class ที่เข้าถึงได้โดยตรง เช่น ui.Main)
// หาก Main.java อยู่ใน Package อื่น เช่น 'main', คุณอาจต้องเปลี่ยน Main เป็น main.Main
// ในโค้ดนี้จะใช้ Main.WIDTH และ Main.HEIGHT โดยสมมติว่า Main Class อยู่ในขอบเขตการเข้าถึง
// หาก Main.java อยู่ใน package "main" ให้เปลี่ยนเป็น import main.Main; และใช้ main.Main.WIDTH

public class GameBoardScreen {
    private final Stage primaryStage;
    private final GameController gameController;
    private final Board board;
    private final List<Player> players;
    private final List<Integer> selectedCharIndices;

    private Label player1MoneyLabel;
    private Label player2MoneyLabel;

    // *** สำหรับจัดการ Token บนกระดาน ***
    private final List<ImageView> playerTokens;
    private final GridPane boardGrid;

    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png"
    };
    private static final String RESOURCE_PATH = "/Character/";

    public GameBoardScreen(Stage primaryStage, GameController gameController, List<Player> players, List<Integer> selectedCharIndices) {
        this.primaryStage = primaryStage;
        this.gameController = gameController;
        this.board = gameController.getBoard();
        this.players = players;
        this.selectedCharIndices = selectedCharIndices;

        this.playerTokens = new ArrayList<>();
        this.boardGrid = createBoardGrid();
        initializePlayerTokens();
    }

    /**
     * สร้างและจัดเก็บ ImageView สำหรับผู้เล่นแต่ละคน
     */
    private void initializePlayerTokens() {
        for (int i = 0; i < players.size(); i++) {
            ImageView token = createCharacterImageView(selectedCharIndices.get(i));
            token.setFitWidth(30);
            token.setFitHeight(30);
            token.setId("player-token-" + i);
            playerTokens.add(token);
        }
    }

    /**
     * สร้าง Scene หลักสำหรับกระดานเกม
     */
    public Scene createGameScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-background");

        // 3. สร้างปุ่มทอยลูกเต๋า (Roll Button)
        Button rollButton = new Button("Roll Dice!");
        rollButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rollButton.setMinWidth(150);
        rollButton.setMinHeight(150);
        rollButton.setId("roll-button");

        // เชื่อมต่อปุ่ม Roll กับ GameController
        rollButton.setOnAction(e -> {
            Player activePlayer = gameController.takeNormalTurn();
            updatePlayerMoney(activePlayer);
            updatePlayerTokens(); // <-- อัปเดต Token
            // ลบการใช้ System.out.println() เพื่อให้โค้ดสะอาดขึ้น
        });

        // 4. สร้าง StackPane เพื่อวางปุ่มทับกระดานตรงกลาง
        StackPane centerStack = new StackPane(boardGrid, rollButton);
        // *** ใช้ Main.WIDTH และ Main.HEIGHT โดยสมมติว่า Main Class อยู่ในขอบเขตการเข้าถึง ***
        centerStack.setMaxWidth(Main.WIDTH - 100);
        centerStack.setMaxHeight(Main.HEIGHT - 100);

        root.setCenter(centerStack);

        // 5. วาง Player Panels (HBox)
        if (players.size() >= 1) {
            HBox player1Panel = createPlayerPanel(players.get(0), selectedCharIndices.get(0), true);
            root.setTop(player1Panel);
            BorderPane.setAlignment(player1Panel, Pos.TOP_LEFT);
            BorderPane.setMargin(player1Panel, new Insets(10));
        }

        if (players.size() >= 2) {
            HBox player2Panel = createPlayerPanel(players.get(1), selectedCharIndices.get(1), false);
            root.setBottom(player2Panel);
            BorderPane.setAlignment(player2Panel, Pos.BOTTOM_RIGHT);
            BorderPane.setMargin(player2Panel, new Insets(10));
        }

        // *** เรียกครั้งแรกเพื่อวาง Token ที่ช่อง Go (0) ***
        updatePlayerTokens();

        Scene gameScene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return gameScene;
    }


    /**
     * แก้ไขการลบ Token ออกจาก Parent เดิมก่อน
     */
    public void updatePlayerTokens() {
        // 1. ลบ Token เก่าออกจาก Parent (StackPane) ที่มันอยู่ก่อน
        for (ImageView token : playerTokens) {
            // ตรวจสอบว่า Token มี Parent และ Parent เป็น StackPane
            if (token.getParent() instanceof StackPane) {
                StackPane currentCell = (StackPane) token.getParent();
                currentCell.getChildren().remove(token);
            }
        }

        // 2. วาง Token ใหม่ตามตำแหน่งของผู้เล่นปัจจุบัน
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            ImageView token = playerTokens.get(i);
            int currentPos = player.getPosition();

            int col = getColForPosition(currentPos);
            int row = getRowForPosition(currentPos);

            if (row != -1 && col != -1) {
                StackPane targetCell = getCellFromGrid(row, col);
                if (targetCell != null) {
                    targetCell.getChildren().add(token);

                    // จัดตำแหน่งภายใน StackPane
                    StackPane.setAlignment(token, i == 0 ? Pos.BOTTOM_LEFT : Pos.BOTTOM_RIGHT);
                }
            }
        }
    }

    /**
     * ค้นหา StackPane (Cell) จาก Row และ Col
     */
    private StackPane getCellFromGrid(int row, int intcol) {
        for (javafx.scene.Node node : boardGrid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            if (nodeCol != null && nodeRow != null && nodeCol == intcol && nodeRow == row) {
                if (node instanceof StackPane) {
                    return (StackPane) node;
                }
            }
        }
        return null;
    }

    // --- Logic การแปลงตำแหน่ง 1D (0-19) เป็น 2D Grid (Row/Col 0-5) ---
    private int getRowForPosition(int squareIndex) {
        int dim = 6;
        if (squareIndex >= 0 && squareIndex <= 5) return dim - 1;
        if (squareIndex >= 6 && squareIndex <= 9) return dim - 1 - (squareIndex - 5);
        if (squareIndex >= 10 && squareIndex <= 15) return 0;
        if (squareIndex >= 16 && squareIndex <= 19) return 1 + (squareIndex - 16);
        return -1;
    }

    private int getColForPosition(int squareIndex) {
        int dim = 6;
        if (squareIndex >= 0 && squareIndex <= 5) return dim - 1 - squareIndex;
        if (squareIndex >= 6 && squareIndex <= 9) return 0;
        if (squareIndex >= 10 && squareIndex <= 15) return squareIndex - 10;
        if (squareIndex >= 16 && squareIndex <= 19) return dim - 1;
        return -1;
    }
    // --- จบ Logic การแปลงตำแหน่ง ---

    private ImageView createCharacterImageView(int index) {
        ImageView imageView = new ImageView();
        try {
            String fileName = CHARACTER_FILES[index];
            String fullPath = RESOURCE_PATH + fileName;

            // ใช้ Image(getClass().getResourceAsStream(fullPath)) เพื่อโหลดจาก resources
            Image image = new Image(getClass().getResourceAsStream(fullPath));
            imageView.setImage(image);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพตัวละคร: " + CHARACTER_FILES[index]);
            imageView.setFitWidth(50);
        }
        return imageView;
    }

    private HBox createPlayerPanel(Player player, int charIndex, boolean isPlayer1) {

        // สร้าง ImageView สำหรับแสดงรูปตัวละครใน Panel
        ImageView charImageView = createCharacterImageView(charIndex);
        charImageView.setFitWidth(50);

        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label moneyLabel = new Label("Money: $" + player.getMoney());
        moneyLabel.setFont(Font.font("Arial", 16));

        if (isPlayer1) {
            this.player1MoneyLabel = moneyLabel;
        } else {
            this.player2MoneyLabel = moneyLabel;
        }

        VBox infoBox = new VBox(5, nameLabel, moneyLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox panel = new HBox(10, charImageView, infoBox);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(10));
        panel.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)
        )));
        panel.setBackground(new Background(new BackgroundFill(
                isPlayer1 ? Color.LIGHTGREEN : Color.LIGHTCORAL,
                CornerRadii.EMPTY, Insets.EMPTY
        )));
        panel.setPrefWidth(250);

        return panel;
    }

    public void updatePlayerMoney(Player player) {
        if (player == players.get(0)) {
            player1MoneyLabel.setText("Money: $" + player.getMoney());
        } else if (players.size() > 1 && player == players.get(1)) {
            player2MoneyLabel.setText("Money: $" + player.getMoney());
        }
    }

    private VBox createSquareBox(Square square) {
        Label title = new Label(square.getName());
        title.setFont(Font.font("Arial", 10));

        VBox box = new VBox(5, title);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(100, 100);

        if (square.getPosition() == 0) box.setStyle("-fx-background-color: #AAFFAA;");
        else if (square.getPosition() == 5 || square.getPosition() == 10 || square.getPosition() == 15) box.setStyle("-fx-background-color: #FFFF99;");
        else box.setStyle("-fx-background-color: #FFFFFF;");

        box.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1)
        )));

        return box;
    }

    /**
     * สร้าง Grid Layout สำหรับกระดาน 6x6 โดยใช้ StackPane เป็น Cell Container หลัก
     */
    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        int boardSize = board.getBoardSize();
        int dim = 6;

        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {

                int squareIndex = -1;

                // Logic คำนวณ Index ของช่องกระดาน 20 ช่อง
                if (row == dim - 1) {
                    squareIndex = dim - 1 - col;
                }
                else if (col == 0 && row >= 1 && row <= 4) {
                    squareIndex = dim - 1 - row + 5;
                }
                else if (row == 0 && col >= 0 && col <= 5) {
                    squareIndex = col + 10;
                }
                else if (col == dim - 1 && row >= 1 && row <= 4) {
                    squareIndex = row + 15;
                }

                StackPane cell = new StackPane();
                cell.setPrefSize(100, 100);

                if (squareIndex != -1 && squareIndex < boardSize) {
                    Square square = board.getSquare(squareIndex);
                    VBox squareBox = createSquareBox(square);
                    cell.getChildren().add(squareBox);
                    cell.setAlignment(Pos.CENTER);

                    cell.setId("square-cell-" + squareIndex);

                } else {
                    cell.setBackground(Background.EMPTY);
                    cell.setBorder(Border.EMPTY);
                }

                grid.add(cell, col, row);
            }
        }

        return grid;
    }
}