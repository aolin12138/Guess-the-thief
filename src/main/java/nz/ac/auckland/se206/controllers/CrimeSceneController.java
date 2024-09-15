package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class CrimeSceneController {
  private static boolean isFirstTimeInit = true;
  private static boolean hasTalked = false;
  private static boolean walletFound = false;
  private static boolean cameraFound = false;
  private static boolean dashcamFound = false;
  private static boolean isCarFound = false;
  private static GameStateContext context = new GameStateContext();
  private static double timeToCount = 360000;
  private static double timeToCountTo = 360000;
  private static double timeForGuessing = 60000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();

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
  public void initialize() {}

  @FXML
  void onClue1Clicked(MouseEvent event) {}

  @FXML
  void onClue2Clicked(MouseEvent event) {}

  @FXML
  void onClue3Clicked(MouseEvent event) {}

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
}
