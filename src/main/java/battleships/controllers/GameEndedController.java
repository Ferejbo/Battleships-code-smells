package battleships.controllers;

import battleships.models.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameEndedController {
    @FXML
    private Label resultLabel;

    public void initData(Player winner) {
        resultLabel.setText(winner.getName() + " is the winner!!");
    }
}
