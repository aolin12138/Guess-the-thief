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

  @FXML private StackPane indicatorPane;
  @FXML private Button NextPageBtn;
  @FXML private Button PreviousPageBtn;
  @FXML private Button ReturnBtn;
  @FXML private Label timerLabel;

  @FXML
  void onNextPage(ActionEvent event) {}

  @FXML
  void onPreviousPage(ActionEvent event) {}

  @FXML
  void onReturnToCrimeScene(ActionEvent event) {
    Scene sceneOfButton = ReturnBtn.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
    passTimeToCrimeScene(timeToCount);
  }

  @FXML
  public void initialize() {
    if (isFirstTimeInit) {}
    // context.setCrimeController(this); *******NEED THIS
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
                    // if (!(context.getGameState().equals(context.getGameStartedState()))) {
                    //   System.out.println("hello f " + context.getGameState());
                    //   timeline.stop();
                    //   return;
                    // }
                    // if (context.isAllSuspectsSpokenTo()
                    //     && CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGuessingState());
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("guess");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    //   // Stop the timer here, as once the suer switch to guessing state, they
                    // aren't
                    //   // coming back
                    //   timeline.stop();
                    // } else if (!context.isAllSuspectsSpokenTo()
                    //     && CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You did not speak to every suspect during your
                    // investigation!\nWithout"
                    //           + " doing this, the investigation is incomplete!\n"
                    //           + "Click play again to replay.");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // } else if (context.isAllSuspectsSpokenTo()
                    //     && !CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You did not find any clues in the crime scene!\n"
                    //           + "Finding clues is vital to conduting a good investigation!\n"
                    //           + "Click play again to replay");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // } else if (!context.isAllSuspectsSpokenTo()
                    //     && !CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You did not inspect the crime scene for clues or speak to every"
                    //           + " suspect!\n"
                    //           + "These steps are vital in any investigation.\n"
                    //           + "Click play again to replay.");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // }
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  public static void passTimeToCrimeScene(double timeToCount) {
    CrimeSceneController.setTimeToCount(timeToCount);
  }

  public void setContext(GameStateContext context) {
    this.context = context;
  }
}
