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

public class NewspaperController {
  private static boolean isFirstTimeInit = true;
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static GameStateContext context = new GameStateContext();

  /**
   * This method sets the time to count
   *
   * @param timeFromPreviousScene
   */
  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  /**
   * This method passes the time to the crime scene
   *
   * @param timeToCount
   */
  public static void passTimeToCrimeScene(double timeToCount) {
    CrimeSceneController.setTimeToCount(timeToCount);
  }

  @FXML private StackPane indicatorPane;
  @FXML private Button nextPageButton;
  @FXML private Button previousPageButton;
  @FXML private Button returnButton;
  @FXML private Label timerLabel;

  /**
   * This method is called when the next page button is clicked. It will take the user to the next
   * page
   *
   * @param event
   */
  @FXML
  private void onNextPage(ActionEvent event) {}

  /**
   * This method is called when the previous page button is clicked. It will take the user to the
   * previous page
   *
   * @param event
   */
  @FXML
  private void onPreviousPage(ActionEvent event) {}

  /**
   * This method is called when the return button is clicked. It will take the user back to the
   * crime scene
   *
   * @param event
   */
  @FXML
  private void onReturnToCrimeScene(ActionEvent event) {
    Scene sceneOfButton = returnButton.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
    passTimeToCrimeScene(timeToCount);
  }

  /** This method initializes the newspaper scene */
  @FXML
  public void initialize() {
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

                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  public void setContext(GameStateContext context) {
    this.context = context;
  }
}
