package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;

public class StartController {

  @FXML private Button startButton;
  @FXML private Button instructionsButton;
  @FXML private Label ScoreBoardNameLabel1;
  @FXML private Label ScoreBoardNameLabel2;
  @FXML private Label ScoreBoardNameLabel3;
  @FXML private Label ScoreBoardTimeLabel1;
  @FXML private Label ScoreBoardTimeLabel2;
  @FXML private Label ScoreBoardTimeLabel3;
  @FXML private TextField playerNameWindow;

  private Media media =
      new Media(getClass().getResource("/sounds/Intro_brief.mp3").toExternalForm());
  private MediaPlayer mediaPlayer = new MediaPlayer(media);

  // This method will add a score to the scoreboard arraylist.
  // public void addScore(PreviousScore score) {
  //   previousScores.add(score);
  // }

  @FXML
  public void initialize() {
    // enter the scene immediately
    startButton.setDisable(false);

    // This ArrayList will store the scores of the previous rounds.
    ArrayList<String> previousScores = new ArrayList<>();
    // previousScores = Utils.getScoresForStartPage();
    // Check is the previousScores arraylist is empty, if it isn't, display the scores from previous
    // rounds.
    if (previousScores.size() == 2) {
      ScoreBoardNameLabel1.setText(previousScores.get(0));
      ScoreBoardTimeLabel1.setText(previousScores.get(1));
    } else if (previousScores.size() == 4) {
      ScoreBoardNameLabel1.setText(previousScores.get(0));
      ScoreBoardTimeLabel1.setText(previousScores.get(1));
      ScoreBoardNameLabel2.setText(previousScores.get(2));
      ScoreBoardTimeLabel2.setText(previousScores.get(3));
    } else if (previousScores.size() == 6) {
      ScoreBoardNameLabel1.setText(previousScores.get(0));
      ScoreBoardTimeLabel1.setText(previousScores.get(1));
      ScoreBoardNameLabel2.setText(previousScores.get(2));
      ScoreBoardTimeLabel2.setText(previousScores.get(3));
      ScoreBoardNameLabel3.setText(previousScores.get(4));
      ScoreBoardTimeLabel3.setText(previousScores.get(5));
    }

    Platform.runLater(
        () -> {
          mediaPlayer.play();
          mediaPlayer.setOnEndOfMedia(
              () -> {
                startButton.setDisable(false);
              });
        });
  }

  @FXML
  public void onEnterPressed(ActionEvent event) throws IOException {
    // Store the player name
    if (playerNameWindow.getText().isEmpty()) {
      Utils.setPlayerName("Guest Player");
    } else {
      Utils.setPlayerName(playerNameWindow.getText());
    }

    FXMLLoader crimeSceneLoader = new FXMLLoader(App.class.getResource("/fxml/crime.fxml"));
    SceneManager.addRoot(SceneManager.Scene.CRIME, crimeSceneLoader.load());
    SceneManager.setCrimeSceneLoader(crimeSceneLoader);
    // Loads CRIME scene here because timer starts when it is initialised
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  // This method will take the user to the instructions page when they click on the Instructions
  // button
  @FXML
  public void onViewInstructions(ActionEvent event) throws IOException {
    // print the class of the source of the event
    System.out.println(event.getSource().getClass() + "\n\n");
    System.out.println(event.getSource());
    // print the source of the event
    Button button = (Button) event.getSource();
    System.out.println(button.getScene());
    Scene sceneOfButton = button.getScene();
    // set the root of the scene to the instructions page
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.INSTRUCTIONS));
  }

  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }
}
