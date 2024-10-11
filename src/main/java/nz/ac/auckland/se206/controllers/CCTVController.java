package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

/** This class is the controller for the CCTV scene. */
public class CCTVController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static boolean isSeen = false;

  @FXML private Button returnButton;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Label messageLabel;
  @FXML private Label revealImage;
  @FXML private Rectangle brotherFace;

  /**
   * Handles the action when the return button is clicked.
   *
   * @param event the action event that triggered this method
   */
  @FXML
  private void onReturnButtonClicked(ActionEvent event) {
    Scene sceneOfButton = returnButton.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  /** This method intitializes the CCTV scene. */
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
                  // flash the timer red below 30 seconds
                  if (TimelineManager.getTimeToCount() <= 30000) {
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

  /** This method is called when the face is clicked. It will show the recognition label */
  @FXML
  private void onFaceClicked() {
    // When the face is clicked, the faceID label is hidden and the faceID animation label appears,
    // showing the user the suspects identity.
    if (!isSeen) {
      isSeen = true;
      messageLabel.setVisible(false);
      revealImage.setVisible(true);
      TranslateTransition recognitionTransition = new TranslateTransition();
      recognitionTransition.setNode(revealImage);
      recognitionTransition.setDuration(Duration.millis(500));
      recognitionTransition.setFromX(500);
      recognitionTransition.setToX(0);
      recognitionTransition.play();
    }
  }

  @FXML
  void onFaceHoverEnter(MouseEvent event) {
    if (!isSeen) {
      messageLabel.setText("FACIAL RECOGNITION AVAILABLE. CLICK FACE TO RUN.");
      messageLabel.setVisible(true);
    }
  }

  @FXML
  void onFaceHoverExit(MouseEvent event) {
    if (!isSeen) {
      messageLabel.setVisible(false);
    }
  }
}
