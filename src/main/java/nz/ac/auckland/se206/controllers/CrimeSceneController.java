package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class CrimeSceneController {
  private static boolean isFirstTimeInit = true;
  private static boolean hasTalked = false;
  private static boolean walletFound = false;
  private static boolean cameraFound = false;
  private static boolean dashcamFound = false;
  private static boolean isCarFound = false;
  private static boolean isClue1Found = false;
  private static boolean isClue2Found = false;
  private static boolean isClue3Found = false;

  private static GameStateContext context = new GameStateContext();
  private static double timeToCount = 360000;
  private static double timeToCountTo = 360000;
  private static double timeForGuessing = 60000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();

  private Timeline timeline = new Timeline();

  @FXML private Rectangle clue1;
  @FXML private Rectangle clue2;
  @FXML private Rectangle clue3;
  @FXML private Button btnGuess;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Rectangle suscpect2Scene;
  @FXML private Rectangle suspect1Scene;
  @FXML private Rectangle suspect3Scene;

  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      isFirstTimeInit = false;
    }

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
                    // Program switch to guess scene here.
                    System.out.println("Switching to guessing state");
                    context.setState(context.getGuessingState());
                    // Stop the timer here, as once the suer switch to guessing state, they aren't
                    // coming back
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    // play an instruction sound when entering the room for the first time
    // Media media = new Media(getClass().getResource("/sounds/enter_room.mp3").toExternalForm());
    // MediaPlayer mediaPlayer = new MediaPlayer(media);
    // mediaPlayer.play();
    // isFirstTimeInit = false;
    // }
  }

  @FXML
  void onClue1Clicked(MouseEvent event) {
    isClue1Found = true;
  }

  @FXML
  void onClue2Clicked(MouseEvent event) {
    isClue2Found = true;
  }

  @FXML
  void onClue3Clicked(MouseEvent event) {
    isClue3Found = true;
  }

  @FXML
  void onGuessClick(ActionEvent event) throws IOException {
    App.setRoot("guess");
  }

  @FXML
  void onSuspect1Clicked(MouseEvent event) throws IOException {
    // System.out.println(event.getSource().getClass() + "\n\n");
    // System.out.println(event.getSource());
    // App.setRoot("room");
  }

  @FXML
  void onSuspect2Clicked(ActionEvent event) throws IOException {
    App.setRoot("room");
  }

  @FXML
  void onSuspect3Clicked(ActionEvent event) throws IOException {
    App.setRoot("room");
  }

  public static char getClueInvestigationStatus() {
    // 8 possible combinations of clue interaction. From 0 all the way to 3 interacted.
    // Will implement functionality here to return a char based on the number of clues interacted.

    return 'A';
  }
}
