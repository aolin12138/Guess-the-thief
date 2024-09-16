package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;

public class StartController {

  @FXML Button startButton;
  @FXML Button instructionsButton;
  @FXML private Label ScoreboardNameLabel1;
  @FXML private Label ScoreboardNameLabel2;
  @FXML private Label ScoreboardNameLabel3;
  @FXML private Label ScoreboardTimeLabel1;
  @FXML private Label ScoreboardTimeLabel2;
  @FXML private Label ScoreboardTimeLabel3;
  @FXML private TextField playerNameWindow;

  Media media = new Media(getClass().getResource("/sounds/Intro_brief.mp3").toExternalForm());
  MediaPlayer mediaPlayer = new MediaPlayer(media);

  // This method will add a score to the scoreboard arraylist.
  // public void addScore(PreviousScore score) {
  //   previousScores.add(score);
  // }

  @FXML
  public void initialize() {

    // This ArrayList will store the scores of the previous rounds.
    ArrayList<String> previousScores = new ArrayList<>();
    // previousScores = Utils.getScoresForStartPage();
    // Check is the previousScores arraylist is empty, if it isn't, display the scores from previous
    // rounds.
    if (previousScores.size() == 2) {
      ScoreboardNameLabel1.setText(previousScores.get(0));
      ScoreboardTimeLabel1.setText(previousScores.get(1));
    } else if (previousScores.size() == 4) {
      ScoreboardNameLabel1.setText(previousScores.get(0));
      ScoreboardTimeLabel1.setText(previousScores.get(1));
      ScoreboardNameLabel2.setText(previousScores.get(2));
      ScoreboardTimeLabel2.setText(previousScores.get(3));
    } else if (previousScores.size() == 6) {
      ScoreboardNameLabel1.setText(previousScores.get(0));
      ScoreboardTimeLabel1.setText(previousScores.get(1));
      ScoreboardNameLabel2.setText(previousScores.get(2));
      ScoreboardTimeLabel2.setText(previousScores.get(3));
      ScoreboardNameLabel3.setText(previousScores.get(4));
      ScoreboardTimeLabel3.setText(previousScores.get(5));
    }

    Platform.runLater(
        () -> {
          mediaPlayer.play();
          mediaPlayer.setOnEndOfMedia(
              () -> {
                startButton.setDisable(false);
              });
        });
  }

  @FXML
  public void onEnterPressed(ActionEvent event) {
    // Store the player name
    if (playerNameWindow.getText().isEmpty()) {
      Utils.setPlayerName("Guest Player");
    } else {
      Utils.setPlayerName(playerNameWindow.getText());
    }
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  // This method will take the user to the instructions page when they click on the Instructions
  // button
  @FXML
  public void onViewInstructions(ActionEvent event) throws IOException {
    System.out.println(event.getSource().getClass() + "\n\n");
    System.out.println(event.getSource());
    Button button = (Button) event.getSource();
    System.out.println(button.getScene());
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.INSTRUCTIONS));
  }

  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }
}
