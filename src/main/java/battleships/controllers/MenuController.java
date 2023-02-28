package battleships.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;

import battleships.models.filemanagement.FileHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button loadBtn;

    @FXML
    public void initialize() {
        FileHandler fileHandler = new FileHandler();
        try {
            loadBtn.setDisable(!fileHandler.gameIsSaved("savedgame"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void switchToGameView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("battleships/GameView.fxml"));
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        GameController gameController = loader.getController();
        if (node.getId() != null && node.getId().equals("loadBtn")) {
            gameController.loadGame();
        } else {
            gameController.newGame();
        }
        stage.show();
    }
}
