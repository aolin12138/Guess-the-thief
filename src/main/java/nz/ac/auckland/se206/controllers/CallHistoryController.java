package nz.ac.auckland.se206.controllers;

import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

/** This class is the controller for the call history scene. */
public class CallHistoryController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  private MediaPlayer player;

  @FXML private Button homeButton;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Rectangle voiceMailRectangle;

  /**
   * This method is called when the home button is clicked. It will take the user back to the phone
   *
   * @param event Adding more words due to requirements for description.
   */
  @FXML
  private void onHomeButtonClicked(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.PHONE));
  }

  /**
   * This method is called when the phone app is clicked. It will take the user to the call history
   */
  @FXML
  public void initialize() {
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);

    // Update the progress indicator and timer label
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  // Update the progress indicator and timer label
                  ringProgressIndicator.setProgress(TimelineManager.getProgress());
                  timerLabel.setText(Utils.formatTime(TimelineManager.getTimeToCount()));
                  // flash the timer red below 30 seconds
                  if (TimelineManager.getTimeToCount() < 30000) {
                    if ((int) (TimelineManager.getTimeToCount() / 1000) % 2 == 0) {
                      timerLabel.setStyle("-fx-text-fill: rgba(255,0,0,1);");
                    } else {
                      timerLabel.setStyle("-fx-text-fill: rgba(142,3,3,1);");
                    }
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /**
   * This method is called when the voicemail is clicked. It will play the voicemail sound
   *
   * @param event Adding more words due to requirements for description.
   * @throws URISyntaxException Adding more words due to requirements for description.
   */
  @FXML
  void onVoicemailClicked(MouseEvent event) throws URISyntaxException {
    Media sound = new Media(App.class.getResource("/sounds/voicemail.mp3").toURI().toString());
    player = new MediaPlayer(sound);
    player.play();
  }
}
