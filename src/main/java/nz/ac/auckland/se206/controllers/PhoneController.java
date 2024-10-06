package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

public class PhoneController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  private double initialY;

  @FXML private StackPane indicatorPane;

  @FXML private Rectangle callRectangle;
  @FXML private Rectangle callNumberRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private ImageView callHistory;
  @FXML private ImageView callScreen;

  @FXML private StackPane phonePane;
  @FXML private Label timerLabel;

  @FXML private Button backButton;
  @FXML private Rectangle phoneAppRectangle;
  @FXML private Circle endCallButton;

  private String audioPath = "/sounds/voicemail2.mp3";
  private Media audio = new Media(getClass().getResource(audioPath).toString());
  private MediaPlayer mediaPlayer = new MediaPlayer(audio);

  /** This method intializes the phone controller */
  @FXML
  public void initialize() {

    endCallButton.setDisable(true);

    Rectangle clip = new Rectangle(209, 400); // Clip rectangle matching the phone screen size
    phonePane.setClip(clip);

    lockScreen.setOnMousePressed(
        event -> {
          initialY = event.getSceneY(); // Store the initial Y position
        });

    lockScreen.setOnMouseDragged(
        event -> {
          double currentY = event.getSceneY(); // Get the current Y position during the drag
          double dragDistance = initialY - currentY; // Calculate how far the user has dragged

          // Only move the lock screen if the drag distance is significant
          if (dragDistance > 40) { // Threshold to avoid triggering on small movements
            unlock(lockScreen, phonePane.getHeight());
            callRectangle.setDisable(false);
          }
        });

    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);

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

  @FXML
  private void onCallClicked(MouseEvent event) {
    callHistory.setVisible(true);
    callRectangle.setDisable(true);
    callNumberRectangle.setDisable(false);
  }

  /**
   * This method is called when the call number is clicked. It will take the user to the call screen
   *
   * @param event
   */
  @FXML
  private void callNumber(MouseEvent event) {
    // Set the call screen to visible
    callScreen.setVisible(true);
    callNumberRectangle.setDisable(true);

    if (endCallButton.isDisable()) {
      endCallButton.setDisable(false);
    }
    // Play the voicemail sound
    mediaPlayer.play();
    mediaPlayer.setOnEndOfMedia(
        () -> {
          // Stop the voicemail sound if it has finished playing or player leaves the scene
          callScreen.setVisible(false);
          callNumberRectangle.setDisable(false);
          mediaPlayer.stop();
        });
  }

  private void unlock(ImageView lockScreen, double height) {
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lockScreen);
    transition.setToY(-height); // Move the lock screen off the top
    transition.setOnFinished(event -> lockScreen.setVisible(false)); // Hide lock screen
    transition.play();
  }

  /**
   * This method is called when the phone app is clicked. It will take the user to the call history
   *
   * @param event
   */
  @FXML
  void onPhoneAppClicked(MouseEvent event) {
    Scene sceneOfButton = phoneAppRectangle.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CALL_HISTORY));
    // pass the time to the call history controller
  }

  /**
   * This method is called when the back button is clicked. It will take the user back to the crime
   *
   * @param event
   */
  @FXML
  private void onReturnToCrimeScene(ActionEvent event) {
    // create a button instance
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    Platform.runLater(
        () -> {
          // stop the voicemail sound
          mediaPlayer.stop();
          restart();
        });
    // set the root of the scene to the crime scene
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  @FXML
  private void onEndCallButtonClicked(MouseEvent event) {
    callScreen.setVisible(false);
    callNumberRectangle.setDisable(false);
    mediaPlayer.stop();
  }

  public void disableAll() {
    callRectangle.setDisable(true);
    callNumberRectangle.setDisable(true);
  }

  public void restart() {
    if (callScreen.isVisible()) {
      callScreen.setVisible(false);
      callNumberRectangle.setDisable(false);
    }
  }
}
