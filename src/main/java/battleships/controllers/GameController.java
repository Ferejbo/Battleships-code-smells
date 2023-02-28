package battleships.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;

import battleships.models.GameBoard;
import battleships.models.GameUtils;
import battleships.models.Game;
import battleships.models.Position;
import battleships.models.filemanagement.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameController {

    @FXML
    private TextField nameField;

    @FXML
    private Label feedbackLabel, hitLabel, missLabel, shotsRemainingLabel;

    @FXML
    private Pane tiles, hitColorPane, missColorPane;

    @FXML
    private Button randomizeBtn, submitBtn, endTurnBtn;

    private FileHandler fileHandler;

    private String hitColor, missColor;

    private Game game;

    public GameController() {
        fileHandler = new FileHandler();
        game = new Game(GameUtils.boardWidth, GameUtils.boardHeight);

        hitColor = "green";
        missColor = "red";
    }

    @FXML
    public void initialize() {
        createBoard();
        nameField.setText(game.getCurrentPlayer().getName());
    }

    public void loadGame() {
        try {
            game = fileHandler.readGameState(GameUtils.saveGameFileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initialize();
        setUpViewForShootingPart();

    }

    public void newGame() {
        randomizePlacement();
    }

    public void submitBoard() {
        feedbackLabel.setText("");
        try {
            game.submitBoard(nameField.getText());
        } catch (IllegalArgumentException ex) {
            feedbackLabel.setText(ex.getMessage());
            return;
        }
        nameField.setText(game.getCurrentPlayer().getName());
        if (game.getCurrentPlayer() == game.getPlayer1()) {
            setUpViewForShootingPart();
        } else {
            randomizePlacement();
        }
    }

    private void setUpViewForShootingPart() {
        randomizeBtn.setVisible(false);
        hitColorPane.setStyle("-fx-background-color: " + hitColor);
        missColorPane.setStyle("-fx-background-color: " + missColor);
        hitLabel.setText("= Ship hit");
        missLabel.setText("= Miss");
        nameField.setDisable(true);
        submitBtn.setVisible(false);
        endTurnBtn.setVisible(true);

        // All the tiles can call fireShot(String) and pass in its coordinates in the
        // function call
        for (int x = 0; x < game.getBoardWidth(); x++) {
            for (int y = 0; y < game.getBoardHeight(); y++) {
                Node tile = tiles.getChildren().get(x * game.getBoardWidth() + y);
                final int newX = x;
                final int newY = y;
                tile.setOnMouseClicked(e -> fireShot(String.format("%s%s", newX, newY)));
            }
        }
        saveGame();
        updateShootingView();
    }

    public void nextTurn() {
        game.switchPlayer();
        saveGame();

        updateShootingView();
    }

    private void updateShootingView() {
        feedbackLabel.setText("");
        nameField.setText(game.getCurrentPlayer().getName());
        shotsRemainingLabel.setText("Remaining shots: " + game.getCurrentPlayer().getShotsLeft());
        renderBoard(game.getCurrentPlayer().getEnemyBoard());
    }

    private void saveGame() {
        try {
            fileHandler.writeGameState(GameUtils.saveGameFileName, game);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void fireShot(String pos) {
        feedbackLabel.setText("");
        boolean isGameOver;
        try {
            isGameOver = game.fireShot(Character.getNumericValue(pos.charAt(0)),
                    Character.getNumericValue(pos.charAt(1)));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            feedbackLabel.setText(ex.getMessage());
            return;
        }
        saveGame();
        renderBoard(game.getCurrentPlayer().getEnemyBoard());
        shotsRemainingLabel.setText("Remaining shots: " + game.getCurrentPlayer().getShotsLeft());

        if (isGameOver) {
            try {
                gameEnded();
                fileHandler.deleteSave(GameUtils.saveGameFileName);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void gameEnded() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("battleships/GameEndedView.fxml"));
        // Here we reference nameField to get the scene. Any other element in the fxml
        // related to this controller works fine
        Stage stage = (Stage) ((Node) nameField).getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        GameEndedController controller = loader.getController();
        controller.initData(game.getCurrentPlayer());
        stage.show();
    }

    public void randomizePlacement() {
        game.randomizeCurrentBoard();
        renderBoard(game.getCurrentPlayer().getFriendlyBoard());
    }

    private void createBoard() {
        tiles.getChildren().clear();
        double preferredWidth = tiles.getPrefWidth() / game.getBoardWidth();
        double preferredHeight = tiles.getPrefHeight() / game.getBoardHeight();
        for (int x = 0; x < game.getBoardWidth(); x++) {
            for (int y = 0; y < game.getBoardHeight(); y++) {
                Pane tile = new Pane();
                tile.setTranslateX(x * preferredWidth);
                tile.setTranslateY(y * preferredHeight);
                tile.setPrefWidth(preferredWidth);
                tile.setPrefHeight(preferredHeight);
                tile.setUserData(x + y);
                tiles.getChildren().add(tile);
            }
        }
    }

    private void renderBoard(GameBoard board) {
        String color;
        Node tile;
        for (int x = 0; x < game.getBoardWidth(); x++) {
            for (int y = 0; y < game.getBoardHeight(); y++) {
                tile = tiles.getChildren().get(x * game.getBoardWidth() + y);
                color = "#f0f0f5";
                Position square = board.getSquare(x, y);
                if (square.getContainsShip() && game.getIsPlacementPhase()) {
                    color = "#333399";
                } else if (square.getIsHit() && !game.getIsPlacementPhase()) {
                    color = missColor;
                    if (square.getContainsShip()) {
                        color = hitColor;
                    }
                }
                tile.setStyle(String.format("-fx-background-color: %s; -fx-border-color:#000000; -fx-border-width:1px;",
                        color));
            }
        }
    }
}
