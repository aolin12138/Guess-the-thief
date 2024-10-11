package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;

/**
 * The Person class represents an individual with various attributes.
 *
 * <p>This class provides methods to access and manipulate the attributes of a person, such as their
 * name, age, and interview status.
 */
public class StartController {

  @FXML private Button startButton;
  @FXML private Label gameTitle;
  @FXML private Label scoreBoardNameLabel1;
  @FXML private Label scoreBoardNameLabel2;
  @FXML private Label scoreBoardNameLabel3;
  @FXML private Label scoreBoardTimeLabel1;
  @FXML private Label scoreBoardTimeLabel2;
  @FXML private Label scoreBoardTimeLabel3;
  @FXML private TextField playerNameWindow;

  private Media media =
      new Media(getClass().getResource("/sounds/enter_name.mp3").toExternalForm());
  private MediaPlayer mediaPlayer = new MediaPlayer(media);

  /**
   * This method is called when the start scene is loaded. It will display the scores of the
   * previous rounds.
   */
  @FXML
  public void initialize() {

    startButton.setCursor(Cursor.HAND);
    startButton.setDisable(true);
    // This ArrayList will store the scores of the previous rounds.
    ArrayList<String> previousScores = new ArrayList<>();
    previousScores = Utils.getScoresForStartPage();
    // Check is the previousScores arraylist is empty, if it isn't, display the scores from previous
    // rounds.
    if (previousScores.size() == 2) {
      scoreBoardNameLabel1.setText(previousScores.get(0));
      scoreBoardTimeLabel1.setText(previousScores.get(1));
      // If there is only one score, display the same score twice.
    } else if (previousScores.size() == 4) {
      scoreBoardNameLabel1.setText(previousScores.get(0));
      scoreBoardTimeLabel1.setText(previousScores.get(1));
      scoreBoardNameLabel2.setText(previousScores.get(2));
      scoreBoardTimeLabel2.setText(previousScores.get(3));
      // If there are two scores, display the scores.
    } else if (previousScores.size() == 6) {
      scoreBoardNameLabel1.setText(previousScores.get(0));
      scoreBoardTimeLabel1.setText(previousScores.get(1));
      scoreBoardNameLabel2.setText(previousScores.get(2));
      scoreBoardTimeLabel2.setText(previousScores.get(3));
      scoreBoardNameLabel3.setText(previousScores.get(4));
      scoreBoardTimeLabel3.setText(previousScores.get(5));
    }

    // Adding the event handler for 'Enter' key on txtInput
    playerNameWindow.setOnKeyPressed(
        new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
              try {
                // Calling the send message function
                onEnterPressed(keyEvent);
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        });
  }

  /**
   * This method is called when the Enter is pressed. It will take the user to the crime scene.
   *
   * @param event extra tag description due missing tag description.
   * @throws IOException extra tag description due missing tag description.
   */
  @FXML
  private void onEnterPressed(Event event) throws IOException {
    // need to check that the user has entered a username, if they haven't, remind them

    // Store the player name
    if (playerNameWindow.getText().strip().isEmpty()) {
      Platform.runLater(
          () -> {
            mediaPlayer.stop();
            mediaPlayer.play();
          });
      return;
    } else {
      Utils.setPlayerName(playerNameWindow.getText());
      playerNameWindow.clear();
    }
    // Load the crime scene
    FXMLLoader crimeSceneLoader = new FXMLLoader(App.class.getResource("/fxml/crime.fxml"));
    SceneManager.addRoot(SceneManager.Scene.CRIME, crimeSceneLoader.load());
    SceneManager.setCrimeSceneLoader(crimeSceneLoader);
    CrimeSceneController.setContext(new GameStateContext());
    // Loads CRIME scene here because timer starts when it is initialised
    try {
      Button button = (Button) event.getSource();
      Scene sceneOfButton = button.getScene();
      Platform.runLater(
          () -> {
            mediaPlayer.stop();
          });
      sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
      TimelineManager.setContext(CrimeSceneController.getContext());
      TimelineManager.startTimer();
      // If the event source is not a button, it is a text field
    } catch (ClassCastException e) {
      TextField field = (TextField) event.getSource();
      Scene sceneOfField = field.getScene();
      Platform.runLater(
          () -> {
            mediaPlayer.stop();
          });
      sceneOfField.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
      TimelineManager.setContext(CrimeSceneController.getContext());
      TimelineManager.startTimer();
    }
  }

  /**
   * This method is called when the key is pressed. It will print the key that is pressed.
   *
   * @param event The event that is triggered when the key is pressed.
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * This method is called when the key is released. It will print the key that is released.
   *
   * @param event The event that is triggered when the key is released.
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
    if (playerNameWindow.getText().isEmpty()) {
      startButton.setDisable(true);
    } else {
      startButton.setDisable(false);
    }
  }
}
