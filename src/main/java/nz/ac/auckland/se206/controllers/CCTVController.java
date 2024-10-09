package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

public class CCTVController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  @FXML private Button returnButton;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Label recognition;
  @FXML private Rectangle brotherFace;

  /**
   * This method is called when the return button is clicked. It will take the user back to the
   * crime
   *
   * @param event
   */
  @FXML
  private void onReturnButtonClicked(ActionEvent event) {
    Scene sceneOfButton = returnButton.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  /** This method intitializes the CCTV scene */
  @FXML
  public void initialize() {
    returnButton.setCursor(Cursor.HAND);
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);
    // Update the progress indicator and timer label
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  ringProgressIndicator.setProgress(TimelineManager.getProgress());
                  timerLabel.setText(Utils.formatTime(TimelineManager.getTimeToCount()));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /** This method is called when the face is clicked. It will show the recognition label */
  @FXML
  private void onFaceClicked() {
    recognition.setVisible(true);
  }
}
