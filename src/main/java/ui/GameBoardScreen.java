package ui;

import game.GameController;
import model.*;
import model.Card;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform; // *** สำคัญ: สำหรับ Platform.runLater() ***
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameBoardScreen {
    private final Stage primaryStage;
    private final GameController gameController;
    private final Board board;
    private final List<Player> players;
    private final List<Integer> selectedCharIndices;

    private Label player1MoneyLabel;
    private Label player2MoneyLabel;
    private Label playerTurnLabel;

    private final List<ImageView> playerTokens;
    private final GridPane boardGrid;

    // ... (Constants)
    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MOVEMENT_DELAY_MS = 300;

    // สมมติขนาดหน้าจอ
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;

    public GameBoardScreen(Stage primaryStage, GameController gameController, List<Player> players, List<Integer> selectedCharIndices) {
        this.primaryStage = primaryStage;
        this.gameController = gameController;
        this.board = gameController.getBoard();
        this.players = gameController.getPlayers();
        this.selectedCharIndices = selectedCharIndices;

        this.playerTokens = new ArrayList<>();
        this.boardGrid = createBoardGrid();
        initializePlayerTokens();
    }

    private void initializePlayerTokens() {
        Color[] shadowColors = {Color.LIMEGREEN, Color.RED};

        for (int i = 0; i < players.size(); i++) {
            ImageView token = createCharacterImageView(selectedCharIndices.get(i));
            token.setFitWidth(30);
            token.setFitHeight(30);
            token.setId("player-token-" + i);

            if (i < shadowColors.length) {
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(shadowColors[i]);
                dropShadow.setRadius(8.0);
                dropShadow.setSpread(0.4);
                dropShadow.setOffsetX(0);
                dropShadow.setOffsetY(0);
                token.setEffect(dropShadow);
            }
            playerTokens.add(token);
        }
    }

    public Scene createGameScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-background");

        // Roll Button
        Button rollButton = new Button("Roll Dice!");
        rollButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        rollButton.setMinWidth(150);
        rollButton.setMinHeight(150);
        rollButton.setId("roll-button");
        rollButton.setOnAction(e -> {
            rollButton.setDisable(true);
            startTurnAndAnimate(rollButton);
        });

        // Player Turn Label
        playerTurnLabel = new Label("Turn: " + gameController.getCurrentPlayer().getName());
        playerTurnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerTurnLabel.setStyle("-fx-text-fill: #333333;");

        VBox centerControl = new VBox(10, playerTurnLabel, rollButton);
        centerControl.setAlignment(Pos.CENTER);

        StackPane centerStack = new StackPane(boardGrid, centerControl);
        root.setCenter(centerStack);

        // Player Panels
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

        updatePlayerTokens();
        updateTurnDisplay(rollButton); // Initial update for color

        Scene gameScene = new Scene(root, WIDTH, HEIGHT);
        try {
            gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Warning: Could not load style.css. Ensure it is in the resource path.");
        }
        return gameScene;
    }

    /**
     * เริ่มเทิร์น ทอยลูกเต๋า และจัดการแอนิเมชัน
     */
    private void startTurnAndAnimate(Button rollButton) {
        Player activePlayer = gameController.getCurrentPlayer();

        // 1. ตรวจสอบ Jail (NEW LOGIC)
        if (activePlayer.isInJail()) {
            boolean shouldPlayTurn = showJailDialog(activePlayer, rollButton);
            if (!shouldPlayTurn) {
                // ไม่ได้ออกจากคุก -> ข้ามเทิร์น
                gameController.moveToNextPlayer();
                updateTurnDisplay(rollButton);
                return;
            }
            // หาก shouldPlayTurn เป็น true แสดงว่าผู้เล่นจ่ายเงินออกแล้ว ให้ดำเนินการต่อ
        }

        // 2. ตรวจสอบ Skip Turn
        if (activePlayer.getSkipTurns() > 0) {
            showSkipTurnDialog(activePlayer, rollButton);
            return;
        }

        // 3. ทอยลูกเต๋า
        int steps = gameController.getDice().roll();
        System.out.println(activePlayer.getName() + " rolled a " + steps);

        // 4. เริ่มแอนิเมชันการเดิน
        animateMovement(activePlayer, steps, rollButton);
    }

    // *** NEW: Dialog สำหรับ Jail ***
    private boolean showJailDialog(Player player, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You are in Jail!");
        alert.setHeaderText(player.getName() + " is in Jail.");
        alert.setContentText("Do you want to pay $50 to get out and roll the dice?");

        ButtonType payButton = new ButtonType("Pay $50 and Roll");
        ButtonType stayButton = new ButtonType("Stay in Jail", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(payButton, stayButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == payButton) {
            // ผู้เล่นเลือกจ่ายเงิน
            if (player.getMoney() >= 50 && player.deductMoney(50)) {
                player.releaseFromJail();
                updatePlayerMoney(player);
                showSimpleAlert("Jail Release", player.getName() + " paid $50 and is now released!");
                return true; // ออกจากคุกสำเร็จ -> เล่นต่อได้

            } else {
                // เงินไม่พอ
                showSimpleAlert("Transaction Failed", player.getName() + " cannot afford to pay $50 and stays in Jail.");
                return false; // ไม่ได้ออกจากคุก -> ต้องข้ามเทิร์น
            }
        } else {
            // ผู้เล่นเลือกอยู่ต่อ
            showSimpleAlert("Jail Stay", player.getName() + " chose to stay in Jail for this turn.");
            return false; // อยู่ในคุก -> ต้องข้ามเทิร์น
        }
    }

    private void showSkipTurnDialog(Player player, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Skip Turn");
        alert.setHeaderText(player.getName() + " must skip turn!");
        alert.setContentText("You have " + player.getSkipTurns() + " turn(s) left to skip.");

        player.setSkipTurns(player.getSkipTurns() - 1);

        alert.showAndWait();

        // จบเทิร์นทันทีหลังจาก Skip
        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    /**
     * สร้าง Timeline สำหรับเดิน Token ทีละช่อง
     */
    private void animateMovement(Player player, int steps, Button rollButton) {
        Timeline timeline = new Timeline();
        int boardSize = gameController.getBoard().getBoardSize();

        for (int i = 1; i <= steps; i++) {
            final int step = i;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(MOVEMENT_DELAY_MS * step),
                    event -> {
                        int oldPosition = player.getPosition();
                        int newPosition = (oldPosition + 1) % boardSize;

                        player.warp(newPosition);

                        // ตรวจสอบการผ่าน Go (รับเงิน $200) ด้วยตัวเอง
                        if (newPosition == 0 && oldPosition != 0) {
                            player.addMoney(100);
                            updatePlayerMoney(player);
                            System.out.println(player.getName() + " passed Go! Collected $200.");
                        }
                        updatePlayerTokens();
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // KeyFrame สุดท้าย: เมื่อแอนิเมชั่นจบ
        timeline.setOnFinished(event -> {
            // *** FIX: ใช้ Platform.runLater() เพื่อหลีกเลี่ยง IllegalStateException ***
            Platform.runLater(() -> {
                handleLandedSquareInteraction(player, rollButton);
            });
        });

        timeline.play();
    }

    // =========================================================================
    // *** Interactive Logic Handling ***
    // =========================================================================

    private void handleLandedSquareInteraction(Player player, Button rollButton) {
        Square landedSquare = board.getSquare(player.getPosition());

        // 1. เรียก landOn() เพื่อให้ Console Log ทำงาน และแสดงข้อความเบื้องต้น
        landedSquare.landOn(player);

        // 2. จัดการ Logic ตามประเภทช่อง
        if (landedSquare instanceof Property) {
            handlePropertyAction(player, (Property) landedSquare, rollButton);
        } else if (landedSquare instanceof CardSquare) {
            handleCardAction(player, rollButton);
        } else if (landedSquare instanceof AirplaneSquare) {
            handleAirplaneAction(player, rollButton);
        } else {
            // สำหรับ Go, Jail (Just Visiting), Lottery, Go to Jail (ซึ่ง goToJail ทำงานใน landOn แล้ว)
            checkBankruptcy(player);
            gameController.moveToNextPlayer();
            updateTurnDisplay(rollButton);
        }
    }

    private void handlePropertyAction(Player player, Property property, Button rollButton) {
        // ตรวจสอบ Rentable และการจ่ายค่าเช่า
        if (property.getOwner() != null && property.getOwner() != player) {

            // 1. Logic การจ่ายค่าเช่า (รวมถึง Buyout)
            int rent = property.calculateRent(0);

            Alert rentAlert = new Alert(Alert.AlertType.CONFIRMATION);
            rentAlert.setTitle("Property Owned");
            rentAlert.setHeaderText("Pay Rent! $" + rent);
            rentAlert.setContentText(player.getName() + " landed on " + property.getOwner().getName() + "'s property (" + property.getName() + "). Pay rent now?");

            Optional<ButtonType> rentResult = rentAlert.showAndWait();
            if (rentResult.isPresent() && rentResult.get() == ButtonType.OK) {
                if (player.deductMoney(rent)) {
                    property.getOwner().addMoney(rent);
                } else {
                    // ล้มละลายหากเงินไม่พอจ่ายค่าเช่า
                    player.addMoney(-player.getMoney());
                }
                updatePlayerMoney(player);
            }

            // 2. Logic Buyout (ถ้าไม่ใช่ Landmark และผู้เล่นมีเงินพอ)
            if (player.getMoney() > 0 && !property.isLandmark()) {
                int buyoutPrice = (int)( property.getPrice() * 1.5);
                if (player.getMoney() >= buyoutPrice) {
                    Alert buyoutAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    buyoutAlert.setTitle("Buyout Opportunity");
                    buyoutAlert.setHeaderText("Buyout " + property.getName() + "?");
                    buyoutAlert.setContentText("Price: $" + buyoutPrice + ". Do you want to buy it from " + property.getOwner().getName() + "?");

                    Optional<ButtonType> buyoutResult = buyoutAlert.showAndWait();
                    if (buyoutResult.isPresent() && buyoutResult.get() == ButtonType.OK) {
                        if (player.deductMoney(buyoutPrice)) {
                            property.getOwner().addMoney(buyoutPrice);
                            property.setOwner(player);
                            property.setLandmark(false);
                            updateSquareBox(property);
                            System.out.println("✅ " + player.getName() + " successfully bought out " + property.getName() + "!");
                        }
                    }
                }
            }

            checkBankruptcy(player);
            gameController.moveToNextPlayer();
            updateTurnDisplay(rollButton);

        } else if (property.getOwner() == player) {
            // ช่องตัวเอง: เสนอให้อัพเกรดเป็น Landmark
            if (!property.isLandmark()) {
                showUpgradePropertyDialog(player, property, rollButton);
            } else {
                gameController.moveToNextPlayer();
                updateTurnDisplay(rollButton);
            }

        } else {
            // ช่องว่าง: เสนอให้ซื้อ
            showBuyPropertyDialog(player, property, rollButton);
        }
    }

    private void showBuyPropertyDialog(Player player, Property property, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Buy Property");
        alert.setHeaderText("Unowned Property: " + property.getName());
        alert.setContentText("Price: $" + property.getPrice() + ". Would you like to buy it?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (player.deductMoney(property.getPrice())) {
                property.setOwner(player);
                updateSquareBox(property);
            } else {
                showSimpleAlert("Transaction Failed", "You don't have enough money to buy " + property.getName() + "!");
            }
            checkBankruptcy(player);
            updatePlayerMoney(player);
        }

        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    private void showUpgradePropertyDialog(Player player, Property property, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Upgrade Property");
        alert.setHeaderText("Your Property: " + property.getName());
        alert.setContentText("Upgrade to Landmark? Cost: $" + property.getUpgradeCost() + ". (Rent x3)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (player.deductMoney(property.getUpgradeCost())) {
                property.setLandmark(true);
                updateSquareBox(property);
            } else {
                showSimpleAlert("Transaction Failed", "You don't have enough money to upgrade " + property.getName() + "!");
            }
            checkBankruptcy(player);
            updatePlayerMoney(player);
        }

        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    private void handleCardAction(Player player, Button rollButton) {
        Card drawnCard = gameController.getCardManager().drawCard();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw Card");
        alert.setHeaderText("You drew a card!");
        alert.setContentText("Effect: " + drawnCard.getDescription());
        alert.showAndWait();

        drawnCard.execute(player, board);

        updatePlayerMoney(player);
        updatePlayerTokens(); // อัปเดตตำแหน่งหากมีการย้ายตัวละคร

        // หลังจาก Execute Card ต้องมีการตรวจสอบและอัปเดต UI ที่เกี่ยวข้อง
        checkBankruptcy(player);
        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    private void handleAirplaneAction(Player player, Button rollButton) {
        // สร้างรายการช่องทั้งหมดให้เลือก
        List<Square> allSquares = board.getSquares();
        List<String> choices = allSquares.stream()
                .map(s -> "[" + s.getPosition() + "] " + s.getName())
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Airplane Flight");
        dialog.setHeaderText("Select your destination to fly to:");
        dialog.setContentText(player.getName() + ", choose a square:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int targetPosition = Integer.parseInt(result.get().split("]")[0].substring(1));

            // เรียกใช้เมธอดใน GameController เพื่อคำนวณการเดินและเก็บเงินผ่าน Go
            gameController.handleAirplaneAction(player, targetPosition);

            updatePlayerMoney(player);
            updatePlayerTokens();

            // Logic ต่อไป: จัดการ action ที่ช่องปลายทาง
            Square landedSquare = board.getSquare(player.getPosition());
            landedSquare.landOn(player); // เรียก landOn เพื่อให้เกิด Console Log และเตรียมปฏิสัมพันธ์

            if (landedSquare instanceof Property) {
                // ถ้าลงช่อง Property ต้องไปเข้า Logic ซื้อ/เช่าอีกครั้ง
                handlePropertyAction(player, (Property) landedSquare, rollButton);
                return;
            }
        } else {
            showSimpleAlert("Action Required", "You must select a destination to fly. Turn skipped.");
        }

        checkBankruptcy(player);
        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    private void checkBankruptcy(Player player) {
        if (player.getMoney() <= 0) {
            showSimpleAlert("Game Over!", player.getName() + " has gone BANKRUPT! The other player WINS!");
        }
    }

    private void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateTurnDisplay(Button rollButton) {
        Player nextPlayer = gameController.getCurrentPlayer();
        playerTurnLabel.setText("Turn: " + nextPlayer.getName());
        rollButton.setDisable(false);

        Color playerColor = (nextPlayer == players.get(0)) ? Color.LIMEGREEN : Color.RED;
        playerTurnLabel.setStyle("-fx-text-fill: white; -fx-background-color: " + toHexString(playerColor) + "; -fx-padding: 5; -fx-border-radius: 5;");
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // =========================================================================
    // *** Existing Methods (Board UI) ***
    // =========================================================================

    public void updatePlayerTokens() {
        for (ImageView token : playerTokens) {
            if (token.getParent() instanceof StackPane) {
                ((StackPane) token.getParent()).getChildren().remove(token);
            }
        }

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
                    StackPane.setAlignment(token, i == 0 ? Pos.BOTTOM_LEFT : Pos.BOTTOM_RIGHT);
                }
            }
        }
    }

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

    private VBox createSquareBox(Square square) {
        Label title = new Label(square.getName());
        title.setFont(Font.font("Arial", 10));

        VBox box = new VBox(5, title);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(100, 100);

        updateSquareBoxStyle(box, square);

        box.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1)
        )));

        return box;
    }

    /**
     * Helper method to set style based on Square type and ownership
     */
    private void updateSquareBoxStyle(VBox box, Square square) {
        String backgroundColor = "#FFFFFF";
        String borderColor = "#000000";

        if (square.getPosition() == 0) {
            backgroundColor = "#AAFFAA"; // Go (เขียวอ่อน)
        } else if (square instanceof JailSquare) {
            backgroundColor = "#ADD8E6"; // Jail (น้ำเงินพาสเทล)
        } else if (square instanceof CardSquare) {
            backgroundColor = "#F08080"; // Card (แดงพาสเทล)
        } else if (square instanceof AirplaneSquare) {
            backgroundColor = "#A9A9A9"; // Airport (เทา)
        } else if (square.getPosition() == 10) {
            backgroundColor = "#FFFF99"; // Lottery (เหลืองอ่อน)
        } else if (square.getPosition() == 15) { // Assuming Go To Jail is at 15
            backgroundColor = "#FFFF99"; // Go To Jail (เหลืองอ่อน)
        } else if (square instanceof Property) {
            Property property = (Property) square;
            if (property.getOwner() != null) {
                if (property.getOwner() == players.get(0)) {
                    backgroundColor = "#E6FFE6";
                    borderColor = "#2E8B57";
                } else if (players.size() > 1 && property.getOwner() == players.get(1)) {
                    backgroundColor = "#FFE6E6";
                    borderColor = "#FF6347";
                }

                if (property.isLandmark()) {
                    borderColor = "#FFD700";
                    box.setBorder(new Border(new BorderStroke(
                            Color.web(borderColor),
                            BorderStrokeStyle.SOLID,
                            new CornerRadii(10),
                            new BorderWidths(4)
                    )));
                } else {
                    box.setBorder(new Border(new BorderStroke(
                            Color.web(borderColor),
                            BorderStrokeStyle.SOLID,
                            new CornerRadii(10),
                            new BorderWidths(2)
                    )));
                }
            } else {
                box.setBorder(new Border(new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(10),
                        new BorderWidths(1)
                )));
            }
        }

        box.setStyle("-fx-background-color: " + backgroundColor + ";");
    }

    private void updateSquareBox(Square square) {
        int row = getRowForPosition(square.getPosition());
        int col = getColForPosition(square.getPosition());
        StackPane cell = getCellFromGrid(row, col);

        if (cell != null && cell.getChildren().get(0) instanceof VBox) {
            VBox squareBox = (VBox) cell.getChildren().get(0);
            updateSquareBoxStyle(squareBox, square);
        }
    }


    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        int dim = 6;
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                int squareIndex = getIndexFromGrid(row, col, dim);
                StackPane cell = new StackPane();
                cell.setPrefSize(100, 100);

                if (squareIndex != -1 && squareIndex < board.getBoardSize()) {
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

    private int getIndexFromGrid(int row, int col, int dim) {
        // Logic to map grid coordinates to a 20-square loop (dim=6)
        if (row == dim - 1) return dim - 1 - col; // Bottom Row (19 -> 0)
        if (col == 0 && row >= 1 && row <= 4) return dim - 1 - row + 5; // Left Column (5 -> 9)
        if (row == 0 && col >= 0 && col <= 5) return col + 10; // Top Row (10 -> 15)
        if (col == dim - 1 && row >= 1 && row <= 4) return row + 15; // Right Column (16 -> 19)
        return -1;
    }

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

    private ImageView createCharacterImageView(int index) {
        ImageView imageView = new ImageView();
        try {
            String fileName = CHARACTER_FILES[index];
            String fullPath = RESOURCE_PATH + fileName;
            // Use getResource() and toExternalForm() for reliable resource loading in JavaFX
            Image image = new Image(getClass().getResource(fullPath).toExternalForm());
            imageView.setImage(image);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("!!! ERROR: ไม่พบไฟล์ภาพตัวละคร: " + CHARACTER_FILES[index] + ". Error: " + e.getMessage());
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
}