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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class PhoneController {
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static GameStateContext context = new GameStateContext();

  /**
   * This method passes the time to the crime scene
   *
   * @param timeToCount
   */
  public static void passTimeToCrimeScene(double timeToCount) {
    CrimeSceneController.setTimeToCount(timeToCount);
  }

  /**
   * This method passes the time to the call history
   *
   * @param timeToCount
   */
  public static void passTimeToCallHistory(double timeToCount) {
    CallHistoryController.setTimeToCount(timeToCount);
  }

  /**
   * This method sets the time to count
   *
   * @param timeFromPreviousScene
   */
  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  private double initialY;

  @FXML private StackPane indicatorPane;

  @FXML private Rectangle callRectangle;
  @FXML private Rectangle callNumberRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private ImageView callHistory;
  @FXML private ImageView callScreen;
  @FXML private ImageView arrow;

  @FXML private StackPane phonePane;
  @FXML private Label timerLabel;

  @FXML private Button backButton;
  @FXML private Rectangle phoneAppRectangle;

  private String audioPath = "/sounds/voicemail2.mp3";
  private Media audio = new Media(getClass().getResource(audioPath).toString());
  private MediaPlayer mediaPlayer = new MediaPlayer(audio);

  /** This method intializes the phone controller */
  @FXML
  public void initialize() {

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
            callRectangle.setDisable(false);
          }
        });

    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);
    // Timer label is updated here
    if (timeToCount % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeToCount));
    }

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeToCount > 0) {
                    timeToCount--;
                    progress = (int) (100 - ((timeToCountTo - timeToCount) * 100 / timeToCountTo));
                  } else {
                    // Program switch to guess scene here ONLY if clues and suspects have been
                    // correctly interacted with
                    // Before switching state, make sure the game is still in the game started state
                    // and that we havent already switched state. Otherwise it will cause a bug

                    Utils.checkConditions(
                        context,
                        context.isAllSuspectsSpokenTo(),
                        CrimeSceneController.isAnyClueFound(),
                        timeline);
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    TranslateTransition bouncingArrow = new TranslateTransition();
    bouncingArrow.setNode(arrow);
    bouncingArrow.setDuration(Duration.millis(1000));
    bouncingArrow.setCycleCount(TranslateTransition.INDEFINITE);
    bouncingArrow.setByY(-30);
    bouncingArrow.setAutoReverse(true);
    bouncingArrow.play();
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

  @SuppressWarnings("static-access")
  public void setContext(GameStateContext context) {
    this.context = context;
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
    // initialize a call history controller
    CallHistoryController callHistoryController =
        SceneManager.getCallHistoryLoader().getController();
    // set the context of the call history controller
    callHistoryController.setContext(context);
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CALL_HISTORY));
    // pass the time to the call history controller
    passTimeToCallHistory(timeToCount);
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
    passTimeToCrimeScene(timeToCount);
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
