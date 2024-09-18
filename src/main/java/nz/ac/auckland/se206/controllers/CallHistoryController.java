package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class CallHistoryController {
  private static boolean isFirstTimeInit = true;
  private static double timeToCount = 360000;
  private static double timeToCountTo = 360000;
  private static double timeForGuessing = 60000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static GameStateContext context = new GameStateContext();

  @FXML private Button HomeBtn;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;

  @FXML
  void onHomeButtonClicked(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.PHONE));
    passTimeToPhoneScene(timeToCount);
  }

  @FXML
  public void initialize() {
    if (isFirstTimeInit) {}
    // context.setCrimeController(this); *******NEED THIS
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);
    // Timer label is updated here
    if (timeToCount % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
    }

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeToCount > timeForGuessing) {
                    timeToCount--;
                    progress =
                        (int)
                            (100
                                - (((timeToCountTo - timeForGuessing)
                                        - (timeToCount - timeForGuessing))
                                    * 100
                                    / (timeToCountTo - timeForGuessing)));
                  } else if ((timeToCount > 0)) {
                    // Here the timer has exceeded the time for investigation and the game must
                    // switch to the guess scene.
                    System.out.println("Switching to guessing state");
                    context.setState(context.getGuessingState());
                    // Once in guess state, player will never return to crime scene
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  public static void passTimeToPhoneScene(double timeToCount) {
    PhoneController.setTimeToCount(timeToCount);
  }
}
