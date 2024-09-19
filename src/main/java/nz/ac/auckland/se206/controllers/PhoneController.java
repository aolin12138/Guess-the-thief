package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class PhoneController {
  private static boolean isFirstTimeInit = true;
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static GameStateContext context = new GameStateContext();

  @FXML private StackPane indicatorPane;
  @FXML private Button BackBtn;

  @FXML private Rectangle callRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private ImageView callHistory;
  @FXML private StackPane phonePane;
  @FXML private Label timerLabel;

  private double initialY;

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
                  } else if ((timeToCount > 0)) {
                    // Program switch to guess scene here ONLY if clues and suspects have been
                    // correctly interacted with
                    // Before switching state, make sure the game is still in the game started state
                    // and that we havent already switched state. Otherwise it will cause a bug
                    if (!(context.getGameState().equals(context.getGameStartedState()))) {
                      System.out.println("hello a " + context.getGameState());
                      timeline.stop();
                      return;
                    }
                    if (context.isAllSuspectsSpokenTo()
                        && CrimeSceneController.isAnyClueFound()
                        && context.getGameState().equals(context.getGameStartedState())) {
                      context.setState(context.getGuessingState());
                      try {
                        timeline.stop();
                        App.setRoot("guess");
                        return;
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                      // Stop the timer here, as once the suer switch to guessing state, they aren't
                      // coming back
                      timeline.stop();
                    } else if (!context.isAllSuspectsSpokenTo()
                        && CrimeSceneController.isAnyClueFound()
                        && context.getGameState().equals(context.getGameStartedState())) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not speak to every suspect during your investigation!\n"
                              + "Without doing this, the investigation is incomplete!\n"
                              + "Click play again to replay.");
                      try {
                        timeline.stop();
                        App.setRoot("gamelost");
                        return;
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()
                        && context.getGameState().equals(context.getGameStartedState())) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not find any clues in the crime scene!\n"
                              + "Finding clues is vital to conduting a good investigation!\n"
                              + "Click play again to replay");
                      try {
                        timeline.stop();
                        App.setRoot("gamelost");
                        return;
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (!context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()
                        && context.getGameState().equals(context.getGameStartedState())) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not inspect the crime scene for clues or speak to every"
                              + " suspect!\n"
                              + "These steps are vital in any investigation.\n"
                              + "Click play again to replay.");
                      try {
                        timeline.stop();
                        App.setRoot("gamelost");
                        return;
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  @FXML
  private void onCallClicked(MouseEvent event) {
    callHistory.setVisible(true);
  }

  @FXML
  void onReturnToCrimeScene(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
    passTimeToCrimeScene(timeToCount);
  }

  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  public static void passTimeToCrimeScene(double timeToCount) {
    CrimeSceneController.setTimeToCount(timeToCount);
  }

  public static void passTimeToCallHistory(double timeToCount) {
    CallHistoryController.setTimeToCount(timeToCount);
  }

  public void setContext(GameStateContext context) {
    this.context = context;
  }

  private void unlock(ImageView lockScreen, double height) {
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lockScreen);
    transition.setToY(-height); // Move the lock screen off the top
    transition.setOnFinished(event -> lockScreen.setVisible(false)); // Hide lock screen
    transition.play();
  }
}
