package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
import nz.ac.auckland.se206.ClueManager;
import nz.ac.auckland.se206.PhoneClueManager;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

public class PhoneController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  private double initialY;

  @FXML private StackPane indicatorPane;

  @FXML private Rectangle callNumberRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private ImageView callHistory;
  @FXML private ImageView callScreen;
  @FXML private ImageView arrow;

  @FXML private StackPane phonePane;
  @FXML private Label timerLabel;
  @FXML private Label swipeUpText;

  @FXML private Button backButton;
  @FXML private Rectangle phoneAppRectangle;
  @FXML private Circle endCallButton;
  @FXML private ImageView historyImage;
  @FXML private ImageView phoneApp;
  @FXML private ImageView callEnd;

  private String audioPath = "/sounds/voicemail2.mp3";
  private Media audio = new Media(getClass().getResource(audioPath).toString());
  private MediaPlayer mediaPlayer = new MediaPlayer(audio);
  private PhoneClueManager historyImageManager;
  private ClueManager phoneAppManager;
  private PhoneClueManager callEndManager;

  /** This method intializes the phone controller */
  @FXML
  public void initialize() {

    endCallButton.setDisable(true);
    lockScreen.setCursor(Cursor.HAND);
    arrow.setCursor(Cursor.HAND);
    swipeUpText.setCursor(Cursor.HAND);
    backButton.setCursor(Cursor.HAND);

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
            arrow.setVisible(false);
            swipeUpText.setVisible(false);
          }
        });

    arrow.setOnMousePressed(
        event -> {
          initialY = event.getSceneY(); // Store the initial Y position
        });

    arrow.setOnMouseDragged(
        event -> {
          double currentY = event.getSceneY(); // Get the current Y position during the drag
          double dragDistance = initialY - currentY; // Calculate how far the user has dragged

          // Only move the lock screen if the drag distance is significant
          if (dragDistance > 40) { // Threshold to avoid triggering on small movements
            unlock(lockScreen, phonePane.getHeight());
            arrow.setVisible(false);
            swipeUpText.setVisible(false);
          }
        });

    swipeUpText.setOnMousePressed(
        event -> {
          initialY = event.getSceneY(); // Store the initial Y position
        });

    swipeUpText.setOnMouseDragged(
        event -> {
          double currentY = event.getSceneY(); // Get the current Y position during the drag
          double dragDistance = initialY - currentY; // Calculate how far the user has dragged

          // Only move the lock screen if the drag distance is significant
          if (dragDistance > 40) { // Threshold to avoid triggering on small movements
            unlock(lockScreen, phonePane.getHeight());
            arrow.setVisible(false);
            swipeUpText.setVisible(false);
          }
        });

    historyImageManager = new PhoneClueManager(historyImage);
    phoneAppManager = new ClueManager(phoneApp);
    callEndManager = new PhoneClueManager(callEnd);
    styleScene();

    // Add the ring progress indicator to the pane
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

    // Create a bouncing arrow animation for the phone screen
    TranslateTransition bouncingArrow = new TranslateTransition();
    bouncingArrow.setNode(arrow);
    bouncingArrow.setDuration(Duration.millis(1000));
    bouncingArrow.setCycleCount(TranslateTransition.INDEFINITE);
    bouncingArrow.setByY(-30);
    bouncingArrow.setAutoReverse(true);
    bouncingArrow.play();
  }

  /**
   * This method is called when the call history is clicked. It will take the user to the call
   * screen
   *
   * @param event
   */
  @FXML
  private void onCallClicked(MouseEvent event) {
    arrow.setVisible(false);
    swipeUpText.setVisible(false);
    callHistory.setVisible(true);
    phoneApp.setVisible(false);
    callNumberRectangle.setDisable(false);
    historyImage.setVisible(true);
  }

  /**
   * This method is called when the call number is clicked. It will take the user to the call screen
   *
   * @param event
   */
  @FXML
  private void callNumber(MouseEvent event) {
    // Set the call screen to visible
    arrow.setVisible(false);
    swipeUpText.setVisible(false);
    callScreen.setVisible(true);
    callNumberRectangle.setDisable(true);
    historyImage.setVisible(false);
    callEnd.setVisible(true);

    if (endCallButton.isDisable()) {
      endCallButton.setDisable(false);
      endCallButton.setCursor(Cursor.HAND);
    }
    // Play the voicemail sound
    mediaPlayer.play();
    mediaPlayer.setOnEndOfMedia(
        () -> {
          // Stop the voicemail sound if it has finished playing or player leaves the scene
          callScreen.setVisible(false);
          callNumberRectangle.setDisable(false);
          historyImage.setVisible(true);
          callEnd.setVisible(false);
          mediaPlayer.stop();
        });
  }

  /**
   * This method is called when the lock screen is clicked. It will unlock the phone
   *
   * @param lockScreen
   * @param height
   */
  private void unlock(ImageView lockScreen, double height) {
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lockScreen);
    transition.setToY(-height); // Move the lock screen off the top
    transition.setOnFinished(event -> lockScreen.setVisible(false)); // Hide lock screen
    transition.play();
    phoneApp.setVisible(true);
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

  /**
   * This method is called when the end call button is clicked. It will end the call.
   *
   * @param event
   */
  @FXML
  private void onEndCallButtonClicked(MouseEvent event) {
    callScreen.setVisible(false);
    callNumberRectangle.setDisable(false);
    historyImage.setVisible(true);
    callEnd.setVisible(false);
    mediaPlayer.stop();
  }

  @FXML
  public void styleScene() {
    historyImage.setOnMouseEntered(
        e -> {
          historyImageManager.hoverIn();
        });
    historyImage.setOnMouseExited(
        e -> {
          historyImageManager.hoverOut();
        });

    phoneApp.setOnMouseEntered(
        e -> {
          phoneAppManager.hoverIn();
        });
    phoneApp.setOnMouseExited(
        e -> {
          phoneAppManager.hoverOut();
        });
    callEnd.setOnMouseEntered(
        e -> {
          callEndManager.hoverIn();
        });
    callEnd.setOnMouseExited(
        e -> {
          callEndManager.hoverOut();
        });
  }

  /** This method is called when it needs to disable all the rectangles */
  public void disableAll() {
    phoneApp.setVisible(false);
    callNumberRectangle.setDisable(true);
    historyImage.setVisible(false);
    callEnd.setVisible(false);
  }

  /** This method is called when it needs to restart the call screen. */
  public void restart() {
    if (callScreen.isVisible()) {
      callScreen.setVisible(false);
      callNumberRectangle.setDisable(false);
      historyImage.setVisible(false);
      callEnd.setVisible(true);
    }
  }
}
