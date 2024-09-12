package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.PreviousScore;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;

public class StartController {

  @FXML Button startButton;
  @FXML Button instructionsButton;
  @FXML private TextArea scoreboardArea1;
  @FXML private TextArea scoreboardArea2;
  @FXML private TextArea scoreboardArea3;
  @FXML private TextArea scoreboardArea4;
  @FXML private TextArea scoreboardArea5;
  @FXML private TextArea scoreboardArea6;
  @FXML private TextField playerNameWindow;

  // This ArrayList will store the scores of the previous rounds.
  private ArrayList<PreviousScore> previousScores = new ArrayList<PreviousScore>();

  Media media = new Media(getClass().getResource("/sounds/opening_voice.mp3").toExternalForm());
  MediaPlayer mediaPlayer = new MediaPlayer(media);

  // This method will add a score to the scoreboard arraylist.
  public void addScore(PreviousScore score) {
    previousScores.add(score);
  }

  @FXML
  public void initialize() {
    // Check is the previousScores arraylist is empty, if it isn't, display the scores from previous
    // rounds.
    if (previousScores.isEmpty()) {
      // scoreboardArea1.setText("Previous scores will appear here once you play more rounds!");
    } else {
      for (PreviousScore score : previousScores) {
        // scoreboardArea1.appendText(score.getRoundNumber() + " " + score.getTimeUsed() + "\n");
      }
    }
    // to store the scoreboard values.
    // If this is the first round, scoreboard should display a message saying that there are no
    // scores
    // yet.

    Platform.runLater(
        () -> {
          mediaPlayer.play(); /*************** Temporary disable */
          mediaPlayer.setOnEndOfMedia(
              () -> {
                startButton.setDisable(false);
              });
        });
  }

  @FXML
  public void onEnterPressed() {
    // Store the player name
    if (playerNameWindow.getText().isEmpty()) {
      Utils.setPlayerName("Guest Player");
    } else {
      Utils.setPlayerName(playerNameWindow.getText());
    }
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/room.fxml"));
      Parent root = loader.load();
      // RoomController controller = loader.getController();
      // controller.getContext().setRoomController(controller);
      startButton.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // This method will take the user to the instructions page when they click on the Instructions
  // button
  @FXML
  public void onViewInstructions(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
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
