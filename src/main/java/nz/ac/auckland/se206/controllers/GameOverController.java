package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class GameOverController {
    
    @FXML private Pane statsPane;

    @FXML
    public void initialize() {
        // Check is the previousScores arraylist is empty, if it isn't, display the scores from previous
        // rounds.
        // if (previousScores.isEmpty()) {
        //     // scoreboardArea1.setText("Previous scores will appear here once you play more rounds!");
        // } else {
        //     for (PreviousScore score : previousScores) {
        //         // scoreboardArea1.appendText(score.getRoundNumber() + " " + score.getTimeUsed() + "\n");
        //     }
        // }
        // to store the scoreboard values.
        // If this is the first round, scoreboard should display a message saying that there are no
        // scores
        // yet.

    //     ProgressIndicator statsIndicator = new ProgressIndicator();
    // statsIndicator.setMinSize(1, 1);
    // statsPane.getChildren().add(statsIndicator);
        
    }


    @FXML
    public void handleRestartClick(ActionEvent event) throws ApiProxyException, IOException {
        App.setRoot("start");
    }

    @FXML 
    public void onKeyPressed(ActionEvent event) {
        
    }

    @FXML 
    public void onKeyReleased(ActionEvent event) {
        
    }
    
}
