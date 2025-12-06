package ui;

import game.GameController;
import game.Dice;
import model.Board;
import model.JailSquare;
import model.Player;
import model.Square;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow; // *** NEW IMPORT ***

import java.util.List;
import java.util.ArrayList;

// สมมติว่า Main Class อยู่ในขอบเขตการเข้าถึงและมี static final WIDTH/HEIGHT
public class GameBoardScreen {
    private final Stage primaryStage;
    private final GameController gameController;
    private final Board board;
    private final List<Player> players;
    private final List<Integer> selectedCharIndices;

    private Label player1MoneyLabel;
    private Label player2MoneyLabel;

    private final List<ImageView> playerTokens;
    private final GridPane boardGrid;

    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MOVEMENT_DELAY_MS = 300; // ความเร็วในการเดินต่อช่อง (0.3 วินาที)

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

    private void initializePlayerTokens() {
        // กำหนดสีเงาสำหรับผู้เล่น 1 (index 0) และ ผู้เล่น 2 (index 1)
        Color[] shadowColors = {Color.LIMEGREEN, Color.RED}; // เขียวสำหรับ P1, แดงสำหรับ P2

        for (int i = 0; i < players.size(); i++) {
            ImageView token = createCharacterImageView(selectedCharIndices.get(i));
            token.setFitWidth(30);
            token.setFitHeight(30);
            token.setId("player-token-" + i);

            // *** ADD DropShadow Effect ***
            if (i < shadowColors.length) {
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(shadowColors[i]); // ใช้สีตาม index
                dropShadow.setRadius(8.0); // ปรับรัศมี
                dropShadow.setSpread(0.4); // ปรับความกระจายเงา
                dropShadow.setOffsetX(0);
                dropShadow.setOffsetY(0);
                token.setEffect(dropShadow);
            }
            // *** END DropShadow Effect ***

            playerTokens.add(token);
        }
    }

    public Scene createGameScene() {
        BorderPane root = new BorderPane();
        // บรรทัดนี้จะอ้างถึงสไตล์ใน style.css ซึ่งคุณควรเพิ่ม .game-background { -fx-background-color: #f0f0f0; } เข้าไป
        root.getStyleClass().add("game-background");

        Button rollButton = new Button("Roll Dice!");
        rollButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rollButton.setMinWidth(150);
        rollButton.setMinHeight(150);
        rollButton.setId("roll-button");

        // *** เปลี่ยน Logic ให้เรียกเมธอด Animation ***
        rollButton.setOnAction(e -> {
            // ปิดปุ่ม Roll เพื่อป้องกันการคลิกซ้ำระหว่างแอนิเมชัน
            rollButton.setDisable(true);
            startTurnAndAnimate(rollButton);
        });

        StackPane centerStack = new StackPane(boardGrid, rollButton);
        root.setCenter(centerStack);

        // วาง Player Panels (บนซ้าย & ล่างขวา)
        if (players.size() >= 1) {
            HBox player1Panel = createPlayerPanel(players.get(0), selectedCharIndices.get(0), true);
            root.setTop(player1Panel); // <-- ด้านบน
            BorderPane.setAlignment(player1Panel, Pos.TOP_LEFT);
            BorderPane.setMargin(player1Panel, new Insets(10));
        }

        if (players.size() >= 2) {
            HBox player2Panel = createPlayerPanel(players.get(1), selectedCharIndices.get(1), false);
            root.setBottom(player2Panel); // <-- ด้านล่าง
            BorderPane.setAlignment(player2Panel, Pos.BOTTOM_RIGHT);
            BorderPane.setMargin(player2Panel, new Insets(10));
        }

        // เรียกครั้งแรกเพื่อวาง Token ที่ช่อง Go (0)
        updatePlayerTokens();

        Scene gameScene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        return gameScene;
    }

    /**
     * เริ่มเทิร์น ทอยลูกเต๋า และจัดการแอนิเมชัน
     */
    private void startTurnAndAnimate(Button rollButton) {
        // 1. ทอยลูกเต๋า
        Dice dice = gameController.getDice();
        int steps = dice.roll();

        Player activePlayer = gameController.getPlayers().get(gameController.getCurrentPlayerIndex());
        System.out.println(activePlayer.getName() + " rolled a " + steps);

        // 2. เริ่มแอนิเมชันการเดิน
        animateMovement(activePlayer, steps, rollButton);
    }

    /**
     * สร้าง Timeline สำหรับเดิน Token ทีละช่อง
     */
    private void animateMovement(Player player, int steps, Button rollButton) {
        Timeline timeline = new Timeline();
        int boardSize = gameController.getBoard().getBoardSize();

        for (int i = 1; i <= steps; i++) {
            final int step = i;

            // KeyFrame แต่ละอันคือการเดิน 1 ช่อง
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(MOVEMENT_DELAY_MS * step),
                    event -> {

                        int oldPosition = player.getPosition();
                        int newPosition = (oldPosition + 1) % boardSize;

                        // *** ใช้ warp() เพื่อย้ายตำแหน่งโดยไม่เรียก Logic ผ่าน Go ของ Player.move() ***
                        player.warp(newPosition);

                        // ตรวจสอบการผ่าน Go (รับเงิน $200) ด้วยตัวเอง
                        if (newPosition == 0 && oldPosition != 0) {
                            player.addMoney(200);
                            updatePlayerMoney(player);
                            System.out.println(player.getName() + " passed Go! Collected $200.");
                        }

                        // อัปเดต UI Token
                        updatePlayerTokens();
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // KeyFrame สุดท้าย: เมื่อแอนิเมชั่นจบ
        timeline.setOnFinished(event -> {
            // 3. เรียก Logic การลงช่องสุดท้าย (LandOn) และจบเทิร์น
            gameController.finalizeTurnLogic(player);

            // Final UI updates
            updatePlayerMoney(player);
            updatePlayerTokens();

            // 4. เปิดปุ่ม Roll กลับมา
            rollButton.setDisable(false);
        });

        timeline.play();
    }


    /**
     * แก้ไขการลบ Token ออกจาก Parent เดิมก่อนเพื่อป้องกัน duplicate children added
     */
    public void updatePlayerTokens() {
        // 1. ลบ Token เก่าออกจาก Parent (StackPane) ที่มันอยู่ก่อน
        for (ImageView token : playerTokens) {
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
                    // จัดตำแหน่ง Token ให้อยู่มุมซ้ายล่าง/ขวาล่างของช่อง
                    StackPane.setAlignment(token, i == 0 ? Pos.BOTTOM_LEFT : Pos.BOTTOM_RIGHT);
                }
            }
        }
    }

    // --- Helper Methods ---

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

        String backgroundColor = "#FFFFFF"; // สีเริ่มต้น (ขาว)

        // *** ปรับปรุง Logic การกำหนดสีพื้นหลัง ***

        if (square.getPosition() == 0) {
            // Go
            backgroundColor = "#AAFFAA"; // เขียวอ่อน
        } else if (square instanceof JailSquare) {
            // JailSquare (ตำแหน่ง 5) -> สีน้ำเงินพาสเทล
            backgroundColor = "#ADD8E6"; // น้ำเงินอ่อน (Light Blue)
        } else if (square.getPosition() == 10 || square.getPosition() == 15) {
            // Free Parking (10) และ Go to Jail (15) -> มุมอื่น ๆ
            backgroundColor = "#FFFF99"; // เหลืองอ่อน
        } else {
            // ช่องอื่น ๆ ทั่วไป
            backgroundColor = "#FFFFFF";
        }

        box.setStyle("-fx-background-color: " + backgroundColor + ";");

        box.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1)
        )));

        return box;
    }

    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        int boardSize = board.getBoardSize();
        int dim = 6;

        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {

                int squareIndex = -1;

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