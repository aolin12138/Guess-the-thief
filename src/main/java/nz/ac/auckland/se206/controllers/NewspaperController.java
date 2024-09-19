package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class NewspaperController {
  private static boolean isFirstTimeInit = true;
  private static double timeToCount = 360000;
  private static double timeToCountTo = 360000;
  private static double timeForGuessing = 60000;
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
  @FXML private Button NextPageBtn;
  @FXML private Button PreviousPageBtn;
  @FXML private Button ReturnBtn;
  @FXML private Label timerLabel;

  /**
   * This method is called when the next page button is clicked. It will take the user to the next
   * page
   *
   * @param event
   */
  @FXML
  void onNextPage(ActionEvent event) {}

  /**
   * This method is called when the previous page button is clicked. It will take the user to the
   * previous page
   *
   * @param event
   */
  @FXML
  void onPreviousPage(ActionEvent event) {}

  /**
   * This method is called when the return button is clicked. It will take the user back to the
   * crime scene
   *
   * @param event
   */
  @FXML
  void onReturnToCrimeScene(ActionEvent event) {
    Scene sceneOfButton = ReturnBtn.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
    passTimeToCrimeScene(timeToCount);
  }

  /** This method initializes the newspaper scene */
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
                    // Program switch to guess scene here ONLY if clues and suspects have been
                    // correctly interacted with
                    if (context.isAllSuspectsSpokenTo() && CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGuessingState());
                      try {
                        App.setRoot("guess");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                      // Stop the timer here, as once the suer switch to guessing state, they aren't
                      // coming back
                      timeline.stop();
                    } else if (!context.isAllSuspectsSpokenTo()
                        && CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not speak to every suspect during your investigation!\nWithout"
                              + " doing this, the investigation is incomplete!\n"
                              + "Click play again to replay.");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not find any clues in the crime scene!\n"
                              + "Finding clues is vital to conduting a good investigation!\n"
                              + "Click play again to replay");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (!context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not inspect the crime scene for clues or speak to every"
                              + " suspect!\n"
                              + "These steps are vital in any investigation.\n"
                              + "Click play again to replay.");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
