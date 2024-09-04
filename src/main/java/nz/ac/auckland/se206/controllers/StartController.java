package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.PreviousScore;

public class StartController {

  @FXML Button startButton;
  @FXML Button instructionsButton;
  @FXML TextArea scoreboardArea;

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
    if (previousScores.isEmpty()) {
      scoreboardArea.setText("No scores yet.");
    } else {
      for (PreviousScore score : previousScores) {
        scoreboardArea.appendText(score.getRoundNumber() + " " + score.getTimeUsed() + "\n");
      }
    }
    // to store the scoreboard values.
    // If this is the first round, scoreboard should display a message saying that there are no
    // scores
    // yet.

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
  public void onEnterPressed() {
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

  @FXML
  public void onViewInstructions() {}

  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }
}
