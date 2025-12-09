package ui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media; // Import ใหม่
import javafx.scene.media.MediaPlayer; // Import ใหม่
import java.net.URL;
import game.GameController;
import model.*;
import model.cards.Card;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import model.squares.*;

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

    // FIX 1: ประกาศตัวแปร winSound เพื่อเก็บ AudioClip และควบคุมการหยุด
    private AudioClip winSound;

    private final List<ImageView> playerTokens;
    private final GridPane boardGrid;

    // Constants
    private static final String[] CHARACTER_FILES = {
            "character_01.png", "character_02.png", "character_03.png","character_04.png", "character_05.png", "character_06.png"
    };
    private static final String RESOURCE_PATH = "/Character/";
    private static final int MOVEMENT_DELAY_MS = 300;
    private static final String ICON_PATH = "/Image/";

    // Screen Size
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
        SoundManager.playBGM("board.mp3");

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
            playDiceSound();
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
        updateTurnDisplay(rollButton);

        Scene gameScene = new Scene(root, WIDTH, HEIGHT);

        // --- FIX: เพิ่ม Key Event Handler สำหรับการทดสอบ (Cheat Mode) ---
        // ทำให้สามารถกดคีย์บอร์ด 1-6 เพื่อบังคับผลลัพธ์ลูกเต๋าได้
        gameScene.setOnKeyPressed(event -> {
            // ตรวจสอบว่าเป็นปุ่มตัวเลข 1 ถึง 6 หรือไม่
            if (event.getCode().isDigitKey()) {
                try {
                    // event.getCode().getChar() จะคืนค่า String ของตัวเลขที่กด
                    int steps = Integer.parseInt(event.getCode().getChar());
                    if (steps >= 1 && steps <= 6) {
                        // เรียกใช้ฟังก์ชันเดินทดสอบ (ซึ่งจะตั้งค่า forcedResult ใน Dice)
                        handleTestMove(steps);
                        event.consume(); // หยุดการส่งต่อ Event
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        });
        // ------------------------------------------------------------------

        try {
            gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Warning: Could not load style.css. Ensure it is in the resource path.");
        }
        return gameScene;
    }

    // =========================================================================
    // *** Turn Management Logic ***
    // =========================================================================

    private void startTurnAndAnimate(Button rollButton) {
        Player activePlayer = gameController.getCurrentPlayer();

        // 1. ตรวจสอบสถานะติดคุกก่อน
        if (activePlayer.isInJail()) {
            handleJailTurn(activePlayer, rollButton);
            return;
        }

        // 2. ตรวจสอบ Skip Turn
        if (activePlayer.getSkipTurns() > 0) {
            showSkipTurnDialog(activePlayer, rollButton);
            return;
        }

        // 3. ทอยเต๋า (มีอนิเมชัน)
        executeRollAndMove(activePlayer, rollButton);

    }

    private void handleJailTurn(Player player, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("In Jail");
        alert.setHeaderText("You are in Jail!");
        alert.setContentText("Turns left: " + player.getJailTurns() + "\nChoose an action:");

        ButtonType payButton = new ButtonType("Pay $200");
        ButtonType waitButton = new ButtonType("Wait");
        alert.getButtonTypes().setAll(payButton, waitButton);

        styleDialog(alert);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == payButton) {
            if (player.deductMoney(200)) {
                player.releaseFromJail();
                refreshAllPlayersMoney();
                showSimpleAlert("Released!", "You paid $200 bail. You can move now!");

                // จ่ายแล้วเดินต่อเลย
                executeRollAndMove(player, rollButton);
            } else {
                showSimpleAlert("Error", "Not enough money to pay bail!");
                processWaitInJail(player, rollButton);
            }
        } else {
            processWaitInJail(player, rollButton);
        }
    }

    private void processWaitInJail(Player player, Button rollButton) {
        player.decrementJailTurns();

        if (player.getJailTurns() <= 0) {
            player.releaseFromJail();
            showSimpleAlert("Time served", "You served your time. Next turn you will be free.");
        } else {
            showSimpleAlert("Stay in Jail", "You are still in jail. Turns left: " + player.getJailTurns());
        }
        endTurn(player, rollButton);
    }

    /**
     * ทอยลูกเต๋าพร้อม Effect ตัวเลขวิ่ง
     */
    private void executeRollAndMove(Player activePlayer, Button rollButton) {
        // 1. *** ใส่เสียงลูกเต๋าตรงนี้ (เริ่มทอย) ***
        playDiceSound();

        Timeline diceAnimation = new Timeline();
        diceAnimation.setCycleCount(15);

        diceAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50), e -> {
            int randomFace = (int)(Math.random() * 6) + 1;
            rollButton.setText(String.valueOf(randomFace));
        }));

        diceAnimation.setOnFinished(e -> {
            int actualSteps = gameController.getDice().roll();
            System.out.println(activePlayer.getName() + " rolled a " + actualSteps);

            rollButton.setText(String.valueOf(actualSteps));

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> {
                // ส่งต่อไปยัง Animation การเดิน
                animateMovement(activePlayer, actualSteps, rollButton);
            });
            pause.play();
        });

        diceAnimation.play();
    }

    private void showSkipTurnDialog(Player player, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Skip Turn");
        alert.setHeaderText(player.getName() + " must skip turn!");
        alert.setContentText("You have " + player.getSkipTurns() + " turn(s) left to skip.");

        styleDialog(alert);

        player.setSkipTurns(player.getSkipTurns() - 1);
        alert.showAndWait();

        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

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

                        if (newPosition == 0 && oldPosition != 0) {
                            player.addMoney(20);
                            refreshAllPlayersMoney();
                            System.out.println(player.getName() + " passed Go! Collected $20.");
                        }
                        updatePlayerTokens();
                    }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setOnFinished(event -> {
            Platform.runLater(() -> {
                handleLandedSquareInteraction(player, rollButton);
                Square currentSquare = board.getSquare(player.getPosition());

                // ---------------------------------------------------------
                // 3. *** เรียกเสียงตกช่องตรงนี้ (สำคัญ!) ***
                playSquareSound(currentSquare);
                // ---------------------------------------------------------

                // 4. เรียก Logic เมื่อตกช่อง (เช่น จ่ายเงิน/ติดคุก)
                // gameController.handleLandOnSquare(currentSquare, player);

                // 5. เปิดปุ่มให้กดต่อได้ (ถ้าจบเทิร์นแล้ว)
                rollButton.setDisable(false);
            });
        });

        timeline.play();
    }

    // =========================================================================
    // *** Central Interaction Hub ***
    // =========================================================================

    private void handleLandedSquareInteraction(Player player, Button rollButton) {
        Square landedSquare = board.getSquare(player.getPosition());
        System.out.println(">>> Handling interaction at: " + landedSquare.getName() + " [" + player.getPosition() + "]");

        // 1. Logic Model
        landedSquare.landOn(player);

        // Update UI Money
        refreshAllPlayersMoney();

        // 2. Logic UI (Polymorphism)
        if (landedSquare instanceof Property) {
            handlePropertyAction(player, (Property) landedSquare, rollButton);
        } else if (landedSquare instanceof CardSquare) {
            handleCardAction(player, rollButton);
        } else if (landedSquare instanceof AirplaneSquare) {
            handleAirplaneAction(player, rollButton);
        } else {
            handleGeneralSquareAction(player, landedSquare, rollButton);
        }
    }

    private void handlePropertyAction(Player player, Property property, Button rollButton) {
        if (property.getOwner() != null && property.getOwner() != player) {
            // Pay Rent (บังคับจ่าย, ห้ามมีปุ่ม Cancel)
            int rent = property.calculateRent(0);

            // 1. สร้าง Alert พร้อมตั้งค่าให้เหลือแค่ปุ่ม OK เท่านั้น
            Alert rentAlert = new Alert(Alert.AlertType.WARNING); // ใช้ WARNING เพื่อเน้นย้ำถึงค่าปรับ
            rentAlert.setTitle("Mandatory Rent Payment");
            rentAlert.setHeaderText("PAY RENT! $" + rent);
            rentAlert.setContentText(player.getName() + " landed on " + property.getOwner().getName() + "'s property (" + property.getName() + "). Payment is mandatory.");

            // บังคับให้มีแค่ปุ่ม OK เพื่อยอมรับเท่านั้น
            rentAlert.getButtonTypes().setAll(ButtonType.OK);

            styleDialog(rentAlert);
            rentAlert.showAndWait(); // บังคับให้ผู้เล่นต้องกด OK เพื่อดำเนินการต่อ

            // 2. ดำเนินการหักเงินและจ่ายค่าเช่าทันที
            if (player.deductMoney(rent)) {
                // จ่ายสำเร็จ
                property.getOwner().addMoney(rent);
            } else {
                // เงินไม่พอ: เข้าสู่กระบวนการล้มละลาย (จ่ายเงินที่มีทั้งหมด)
                int moneyToPay = player.getMoney();
                property.getOwner().addMoney(moneyToPay);
                player.addMoney(-moneyToPay); // หักเงินทั้งหมดจนเหลือ 0 (เริ่มต้น Bankrupt Logic)
            }
            refreshAllPlayersMoney();

            // Buyout Logic (ส่วนนี้ทำงานต่อจาก Payment ทันที)
            // ... (โค้ด Buyout Logic เดิม) ...
            if (player.getMoney() > 0 && !property.isLandmark()) {
                int buyoutPrice = (int)( property.getPrice() * 1.5);
                if (player.getMoney() >= buyoutPrice) {
                    Alert buyoutAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    buyoutAlert.setTitle("Buyout Opportunity");
                    buyoutAlert.setHeaderText("Buyout " + property.getName() + "?");
                    buyoutAlert.setContentText("Price: $" + buyoutPrice + ". Do you want to buy it?");

                    styleDialog(buyoutAlert);

                    Optional<ButtonType> buyoutResult = buyoutAlert.showAndWait();
                    if (buyoutResult.isPresent() && buyoutResult.get() == ButtonType.OK) {
                        if (player.deductMoney(buyoutPrice)) {
                            property.getOwner().addMoney(buyoutPrice);
                            property.setOwner(player);
                            property.setLandmark(false);
                            updateSquareBox(property);
                            refreshAllPlayersMoney();
                            System.out.println("✅ " + player.getName() + " bought out " + property.getName());
                        }
                    }
                }
            }
            endTurn(player, rollButton);

        } else if (property.getOwner() == player) {
            // Upgrade
            if (!property.isLandmark()) {
                showUpgradePropertyDialog(player, property, rollButton);
            } else {
                endTurn(player, rollButton);
            }

        } else {
            // Buy
            showBuyPropertyDialog(player, property, rollButton);
        }
    }

    private void handleCardAction(Player player, Button rollButton) {
        int oldPosition = player.getPosition();

        Card drawnCard = gameController.getCardManager().drawCard();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw Card");
        alert.setHeaderText("You drew a card!");
        alert.setContentText("Effect: " + drawnCard.getDescription());

        styleDialog(alert);
        alert.showAndWait();

        drawnCard.execute(player, board);

        refreshAllPlayersMoney();
        updatePlayerTokens();

        // Recursion Check: ถ้าย้ายที่จากการ์ด ให้เรียก Hub ใหม่
        int newPosition = player.getPosition();
        if (newPosition != oldPosition) {
            System.out.println(">> Player moved by card. Triggering new square interaction...");
            handleLandedSquareInteraction(player, rollButton);
        } else {
            endTurn(player, rollButton);
        }
    }

    private void handleAirplaneAction(Player player, Button rollButton) {
        List<Square> allSquares = board.getSquares();
        List<String> choices = allSquares.stream()
                .map(s -> "[" + s.getPosition() + "] " + s.getName())
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Airplane Flight");
        dialog.setHeaderText("Welcome to the Airport!");
        dialog.setContentText(player.getName() + ", choose your destination:");

        styleDialog(dialog);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int targetPosition = Integer.parseInt(result.get().split("]")[0].substring(1));
            gameController.handleAirplaneAction(player, targetPosition);

            updatePlayerTokens();
            refreshAllPlayersMoney();

            // Recursion: ลงจอดแล้วเรียก Hub ใหม่
            handleLandedSquareInteraction(player, rollButton);

        } else {
            showSimpleAlert("Flight Cancelled", "You chose to stay at the airport.");
            endTurn(player, rollButton);
        }
    }

    private void handleGeneralSquareAction(Player player, Square landedSquare, Button rollButton) {
        if (landedSquare instanceof TaxSquare) {
            TaxSquare taxSq = (TaxSquare) landedSquare;
            showSimpleAlert("Tax", "You paid tax! Amount: $" + taxSq.getLastTaxPaid());

        } else if (landedSquare instanceof LotterySquare) {
            LotterySquare lotterySq = (LotterySquare) landedSquare;
            showSimpleAlert("Lottery", "You won the lottery! Prize: $" + lotterySq.getLastPrize());

        //} else if (landedSquare instanceof GoToJailSquare) {
            //showSimpleAlert("Arrested", "You are going to Jail!");
            //updatePlayerTokens();

        } else if (landedSquare instanceof JailSquare) {
            if (player.isInJail()) {
                showSimpleAlert("Jail", "You are in Jail!");
            } //else {
            //    showSimpleAlert("Jail", "Just Visiting.");
            //}
        }

        endTurn(player, rollButton);
    }

    // =========================================================================
    // *** Helper Methods & UI Utilities ***
    // =========================================================================

    private void endTurn(Player player, Button rollButton) {
        checkBankruptcy(player);
        gameController.moveToNextPlayer();
        updateTurnDisplay(rollButton);
    }

    private void showBuyPropertyDialog(Player player, Property property, Button rollButton) {
        int price = property.getPrice();

        // 1. ตรวจสอบว่าผู้เล่นมีเงินพอที่จะซื้อหรือไม่
        if (player.getMoney() >= price) {

            // มีเงินพอ: เปิด Dialog ถามซื้อ
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Buy Property");
            alert.setHeaderText("Unowned Property: " + property.getName());
            alert.setContentText("Price: $" + price + ". Would you like to buy it?");

            styleDialog(alert);

            Optional<ButtonType> result = alert.showAndWait();

            // 2. ถ้าผู้เล่นกด OK
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // ดำเนินการซื้อ (หักเงินและเปลี่ยนเจ้าของ)
                if (player.deductMoney(price)) {
                    property.setOwner(player);
                    updateSquareBox(property);
                }
                // ถ้า deductMoney ล้มเหลว (ซึ่งไม่น่าจะเกิดขึ้นเพราะเช็คเงินแล้ว) ก็จะไม่มีการเปลี่ยนแปลง
            }
            // 3. ถ้าผู้เล่นกด Cancel/ปิด Dialog (ไม่ซื้อ) จะจบเทิร์นเลย

        } else {
            // เงินไม่พอ: แสดงข้อความแจ้งให้ทราบเฉยๆ ว่าซื้อไม่ได้
            showSimpleAlert("Cannot Afford", property.getName() + " costs $" + price + ". You do not have enough money to buy it.");
            // **FIX:** ไม่มีการดำเนินการซื้อหรือหักเงินใดๆ
        }

        // อัปเดตเงิน (เผื่อมีการซื้อ) และจบเทิร์น
        refreshAllPlayersMoney();
        endTurn(player, rollButton);
    }

    private void showUpgradePropertyDialog(Player player, Property property, Button rollButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Upgrade Property");
        alert.setHeaderText("Your Property: " + property.getName());
        alert.setContentText("Upgrade to Landmark? Cost: $" + property.getUpgradeCost() + ". (Rent x3)");

        styleDialog(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            int cost = property.getUpgradeCost();

            // ตรวจสอบว่าเงินพอหรือไม่ ก่อนพยายามหักเงิน
            if (player.getMoney() >= cost) {
                // เงินพอ: ดำเนินการอัปเกรด
                if (player.deductMoney(cost)) {
                    property.setLandmark(true);
                    updateSquareBox(property);
                }
            } else {
                // เงินไม่พอ: แสดงข้อความแจ้งว่ายกเลิกการกระทำ
                showSimpleAlert("Upgrade Failed", "Not enough money to upgrade! (Cost: $" + cost + "). Upgrade is cancelled.");
                // **FIX:** ไม่มีการหักเงิน และไม่เกิดการล้มละลาย (เพราะเช็คก่อนแล้ว)
            }
            refreshAllPlayersMoney();
        }

        // สิ้นสุดการปฏิสัมพันธ์บนช่องนี้ ไม่ว่าจะอัปเกรดสำเร็จ ยกเลิก หรือเงินไม่พอ
        endTurn(player, rollButton);
    }

    private void refreshAllPlayersMoney() {
        for (Player p : players) {
            updatePlayerMoney(p);
        }
    }

    // -------------------------------------------------------------------------
    //  GAME OVER & BANKRUPTCY
    // -------------------------------------------------------------------------
    private void checkBankruptcy(Player player) {
        if (player.getMoney() <= 0) {
            showGameOverScreen(player);
        }
    }

    // FIX 2: ปรับเมธอด showGameOverScreen ให้เป็นไปตามกฎ "เกมยาจก" และจัดการเสียง
    private void showGameOverScreen(Player bankruptPlayer) {
        // *** 1. หยุด BGM ของ Board (สมมติว่า SoundManager มี stopBGM) ***
        SoundManager.stopBGM();

        // *** 2. เล่นเพลง Win และเก็บ reference เพื่อให้หยุดได้ ***
        try {
            URL url = getClass().getResource("/soundfx/win.mp3");
            if (url != null) {
                if (winSound != null) winSound.stop(); // หยุดเสียงเก่า เผื่อเรียกซ้ำ
                winSound = new AudioClip(url.toExternalForm());
                winSound.setVolume(0.5);
                winSound.play();
            }
        } catch (Exception e) { System.err.println("Win sound error: " + e.getMessage()); }

        // --- กำหนด Winner/Loser ตามกฎ "เกมยาจก" ---
        Player winner = bankruptPlayer; // ผู้เล่นที่เงินหมดก่อน คือ ผู้ชนะ
        Player loser = players.stream()
                .filter(p -> p != winner)
                .findFirst()
                .orElse(null);

        if (loser == null) {
            loser = winner;
        }

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 40;");

        Label headerLabel = new Label("🏆 GAME OVER 🏆");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        headerLabel.setTextFill(Color.GOLD);

        Label winnerLabel = new Label(winner.getName() + " WINS!");
        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        winnerLabel.setTextFill(Color.WHITE);

        // ข้อความสำหรับกฎยาจก
        Label subLabel = new Label("ไม่เหลืออะไรแล้วชึวิต ลาก่อน...");
        subLabel.setFont(Font.font("Arial", 18));
        subLabel.setTextFill(Color.LIGHTGRAY);

        // Winner Image
        int playerIndex = players.indexOf(winner);
        int charIndex = selectedCharIndices.get(playerIndex);
        ImageView winnerImage = createCharacterImageView(charIndex);
        winnerImage.setFitWidth(200);
        winnerImage.setFitHeight(200);
        winnerImage.setEffect(new DropShadow(20, Color.BLACK));

        // Buttons
        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setPrefSize(150, 50);
        playAgainBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

        Button exitBtn = new Button("Exit Game");
        exitBtn.setPrefSize(150, 50);
        exitBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand;");

        playAgainBtn.setOnAction(e -> {
            // *** FIX 3: หยุดเพลง win.mp3 ทันทีเมื่อกด Play Again และกลับไปหน้าเลือกตัวละคร ***
            if (winSound != null) {
                winSound.stop();
            }

            // 1. สร้าง GameController ใหม่ (เพื่อรีเซ็ตสถานะเกมทั้งหมด)
            GameController newGame = new GameController();

            // 2. สร้าง CharacterSelectScreen ใหม่
            CharacterSelectScreen selectScreen = new CharacterSelectScreen(primaryStage, newGame);

            // 3. เปลี่ยน Scene (CharacterSelectScreen ควรจะเปิดเพลง select.mp3 เอง)
            primaryStage.setScene(selectScreen.createCharacterSelectScene());
        });

        exitBtn.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        HBox buttonsBox = new HBox(20, playAgainBtn, exitBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(headerLabel, winnerImage, winnerLabel, subLabel, buttonsBox);
        Scene gameOverScene = new Scene(layout, WIDTH, HEIGHT);
        primaryStage.setScene(gameOverScene);
    }

    private void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleDialog(alert);
        alert.showAndWait();
    }

    // *** STYLE HELPER ***
    private void styleDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            dialogPane.getStyleClass().add("game-dialog");
        } catch (Exception e) {
            System.err.println("Warning: Could not load style.css for dialog.");
        }
    }

    private void updateTurnDisplay(Button rollButton) {
        Player nextPlayer = gameController.getCurrentPlayer();
        playerTurnLabel.setText("Turn: " + nextPlayer.getName());

        // Reset Button Text
        rollButton.setText("Roll Dice!");
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
    // *** Board Rendering Logic ***
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


    // ใน GameBoardScreen.java
// ...

    private VBox createSquareBox(Square square, int index) { // <--- **FIXED: รับ int index เข้ามา**

        // --- NEW: 1. Label แสดงหมายเลขช่อง (Index Label) ---
        Label indexLabel = new Label("#" + index);
        indexLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        indexLabel.setTextFill(Color.web("#333333"));
        // ----------------------------------------------------

        // 2. สร้าง Label ชื่อช่องเหมือนเดิม
        Label title = new Label(square.getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        // 3. เรียกใช้เมธอดสร้างไอคอน
        ImageView iconView = createSquareIcon(square);

        // 4. สร้าง VBox เพื่อรวมภาพและข้อความ
        VBox box = new VBox(2);

        // **FIX: เพิ่ม indexLabel เป็น Children ตัวแรกเสมอ**
        box.getChildren().add(indexLabel);

        if (iconView != null) {
            // ถ้ามีรูป ให้ใส่รูปตามหลัง index
            box.getChildren().add(iconView);
        }

        // ใส่ชื่อช่องเป็น Children ตัวสุดท้ายเสมอ
        box.getChildren().add(title);
        // --------------------------------------------------------------

        box.setAlignment(Pos.CENTER);
        box.setPrefSize(100, 100);

        // เรียกใช้ Style สีพื้นหลังเดิมของคุณ
        updateSquareBoxStyle(box, square);

        // ตั้งค่า Border พื้นฐาน
        box.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1)
        )));

        return box;
    }

    private void updateSquareBoxStyle(VBox box, Square square) {
        String backgroundColor = "#FFFFFF";
        String borderColor = "#000000";

        if (square.getPosition() == 0) {
            backgroundColor = "#AAFFAA"; // Go
        } else if (square instanceof JailSquare) {
            backgroundColor = "#ADD8E6"; // Jail
        } else if (square instanceof CardSquare) {
            backgroundColor = "#F08080"; // Card
        } else if (square instanceof AirplaneSquare) {
            backgroundColor = "#A9A9A9"; // Airport
        } else if (square instanceof LotterySquare) {
            backgroundColor = "#FFFF99"; // Lottery
        //} else if (square instanceof GoToJailSquare) {
            //backgroundColor = "#FFA500"; // Go To Jail
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
        // 1. คำนวณหาตำแหน่งใน Grid
        int row = getRowForPosition(square.getPosition());
        int col = getColForPosition(square.getPosition());
        int index = square.getPosition(); // Index คือตำแหน่งของช่อง

        // 2. ดึง StackPane ออกมาจาก Grid
        StackPane cell = getCellFromGrid(row, col);

        if (cell != null && !cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof VBox) {
            VBox squareBox = (VBox) cell.getChildren().get(0);

            // 3. ล้างเนื้อหาเก่าออกให้หมด
            squareBox.getChildren().clear();

            // --- NEW: 4. Recreate Index Label ---
            Label indexLabel = new Label("#" + index);
            indexLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            indexLabel.setTextFill(Color.web("#333333"));
            squareBox.getChildren().add(indexLabel);
            // -----------------------------------

            // 5. สร้างไอคอนใหม่
            ImageView iconView = createSquareIcon(square);

            // 6. สร้าง Label ชื่อช่องใหม่
            Label title = new Label(square.getName());
            title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            title.setWrapText(true);
            title.setAlignment(Pos.CENTER);

            // 7. ใส่กลับเข้าไปใน VBox
            if (iconView != null) {
                squareBox.getChildren().add(iconView);
            }
            squareBox.getChildren().add(title);

            // 8. อัปเดตสีและขอบ
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
                    // *** FIX: ส่ง squareIndex เข้าไปเป็นพารามิเตอร์ตัวที่สอง ***
                    VBox squareBox = createSquareBox(square, squareIndex);
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
        if (row == dim - 1) return dim - 1 - col;
        if (col == 0 && row >= 1 && row <= 4) return dim - 1 - row + 5;
        if (row == 0 && col >= 0 && col <= 5) return col + 10;
        if (col == dim - 1 && row >= 1 && row <= 4) return row + 15;
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
        // 1. หาว่าต้องอัปเดต Label ของใคร
        Label targetLabel = null;
        if (player == players.get(0)) {
            targetLabel = player1MoneyLabel;
        } else if (players.size() > 1 && player == players.get(1)) {
            targetLabel = player2MoneyLabel;
        }

        // 2. ถ้าเจอ Label ที่ต้องอัปเดต
        if (targetLabel != null) {
            int oldMoney = getMoneyFromLabel(targetLabel); // เงินที่โชว์อยู่ (ค่าเก่า)
            int newMoney = player.getMoney();              // เงินจริงตอนนี้ (ค่าใหม่)

            // 3. เปรียบเทียบ: ถ้าเงินไม่เท่าเดิม ค่อยเล่นเสียงและเปลี่ยนข้อความ
            if (oldMoney != newMoney) {
                // เล่นเสียงเฉพาะตอนเงินเปลี่ยน
                playSound("lottery.mp3");

                // อัปเดตข้อความ
                targetLabel.setText("Money: $" + newMoney);

                // (Optional) ปรินท์เช็คใน Console
                // System.out.println("Money changed: " + oldMoney + " -> " + newMoney);
            }
        }
    }

    private ImageView createSquareIcon(Square square) {
        String fileName = null;

        // --- ส่วนตรวจสอบ Property (บ้าน/แลนด์มาร์ค) ---
        if (square instanceof Property) {
            Property prop = (Property) square;

            // เช็ค Landmark ก่อน (สำคัญมาก!)
            if (prop.isLandmark()) {
                fileName = "landmark.png"; // ใช้รูปแลนด์มาร์ค
            }
            // ถ้าไม่ใช่ Landmark แต่มีเจ้าของแล้ว
            else if (prop.getOwner() != null) {
                fileName = "house.png";    // ใช้รูปบ้าน
            }
        }

        // --- ส่วนตรวจสอบช่องพิเศษอื่นๆ (เหมือนเดิม) ---
        else if (square instanceof LotterySquare) {
            fileName = "ingots.png";
        } else if (square instanceof TaxSquare) {
            fileName = "bag.png";
        } else if (square instanceof JailSquare) {
            fileName = "jail.png";
        } else if (square instanceof AirplaneSquare) {
            fileName = "plane.png";
        } else if (square instanceof CardSquare) {
            fileName = "q.png";
        }

        // --- โหลดรูปภาพ ---
        if (fileName != null) {
            try {
                // เช็คว่าไฟล์อยู่ใน /Image/ จริงๆ
                Image img = new Image(getClass().getResourceAsStream("/Image/" + fileName));
                ImageView view = new ImageView(img);
                view.setFitWidth(45);
                view.setFitHeight(45);
                view.setPreserveRatio(true);
                return view;
            } catch (Exception e) {
                System.err.println("!!! Error loading icon: " + fileName);
            }
        }
        return null;
    }
    private void playDiceSound() {
        try {
            // โหลดไฟล์เสียงจาก path /Sound/dice.mp3
            URL soundResource = getClass().getResource("/soundfx/dice.mp3");

            if (soundResource != null) {
                AudioClip sound = new AudioClip(soundResource.toExternalForm());
                sound.setVolume(1.0); // ปรับความดัง 0.0 - 1.0
                sound.play();
            } else {
                System.err.println("!!! Error: ไม่พบไฟล์เสียง dice.mp3 ใน /soundfx/");
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }

    }
    private void playSound(String fileName) {
        try {
            // *** แก้ไข: เปลี่ยนจาก /Sound/ เป็น /soundfx/ ***
            URL soundResource = getClass().getResource("/soundfx/" + fileName);

            if (soundResource != null) {
                AudioClip sound = new AudioClip(soundResource.toExternalForm());
                sound.setVolume(1.0);
                sound.play();
            } else {
                System.err.println("!!! ไม่พบไฟล์เสียง: /soundfx/" + fileName);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound [" + fileName + "]: " + e.getMessage());
        }
    }

    /**
     * เช็คประเภทช่องและเล่นเสียงตามที่กำหนด
     */
    private void playSquareSound(Square square) {
        String soundFile = null;

        // 1. เช็คว่าตกช่องแบบไหน
        if (square instanceof CardSquare) {
            soundFile = "card.mp3";
        } else if (square instanceof JailSquare) {
            soundFile = "jail.mp3";
        } else if (square instanceof LotterySquare || square instanceof TaxSquare) {
            soundFile = "lottery.mp3";
        } else if (square instanceof AirplaneSquare) {
            soundFile = "plane.mp3";
        }

        // 2. ถ้ามีชื่อไฟล์ ให้พยายามเล่น
        if (soundFile != null) {
            System.out.println(">> กำลังพยายามเล่นเสียง: " + soundFile); // DEBUG LINE
            try {
                // ต้องใส่ /Sound/ นำหน้าเสมอ
                URL soundResource = getClass().getResource("/soundfx/" + soundFile);

                if (soundResource != null) {
                    AudioClip sound = new AudioClip(soundResource.toExternalForm());
                    sound.setVolume(1.0);
                    sound.play();
                    System.out.println(">> เล่นเสียงสำเร็จ!"); // DEBUG LINE
                } else {
                    System.err.println("!!! หาไฟล์ไม่เจอ: /soundfx/" + soundFile);
                    System.err.println("!!! เช็คว่าไฟล์อยู่ใน src/main/resources/soundfx/ หรือไม่");
                }
            } catch (Exception e) {
                System.err.println("!!! Error ขณะเล่นเสียง: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println(">> ช่องนี้ไม่มีเสียง (Type: " + square.getClass().getSimpleName() + ")");
        }
    }
    /**
     * ดึงค่าเงินที่เป็นตัวเลขออกมาจาก Label (เช่น "Money: $2000" -> 2000)
     */
    private int getMoneyFromLabel(Label label) {
        try {
            String text = label.getText();
            if (text == null || text.isEmpty()) return 0;
            // ลบทุกตัวอักษรที่ไม่ใช่ตัวเลขและเครื่องหมายลบออก
            String numberOnly = text.replaceAll("[^0-9-]", "");
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 0; // กรณี error ให้ถือว่าเป็น 0
        }
    }
    private void handleTestMove(int steps) {
        Button rollButton = (Button) primaryStage.getScene().lookup("#roll-button");
        if (rollButton == null || rollButton.isDisable()) {
            System.out.println("TEST MOVE: Cannot initiate test move while another action is in progress.");
            return;
        }

        Player activePlayer = gameController.getCurrentPlayer();

        // **FIX:** 1. บังคับผลลัพธ์ของ Dice ใน GameController
        // (สมมติว่า gameController มีเมธอด getDice() ที่เข้าถึง Dice object ได้)
        gameController.getDice().setForcedResult(steps);

        // 2. ปิดปุ่ม Roll เพื่อป้องกันการกดซ้อนระหว่าง Animation
        rollButton.setDisable(true);

        // 3. เรียกใช้ขั้นตอนการทอยปกติ (executeRollAndMove)
        // ซึ่งเมธอดนี้จะเรียก Dice.roll() และได้ผลลัพธ์ตามที่บังคับไว้
        executeRollAndMove(activePlayer, rollButton);

        System.out.println("TEST MOVE: " + activePlayer.getName() + " forces roll to " + steps + " steps (Key: " + steps + ")");
    }
}