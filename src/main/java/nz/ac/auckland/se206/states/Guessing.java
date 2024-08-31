package nz.ac.auckland.se206.states;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The Guessing state of the game. Handles the logic for when the player is making a guess about the
 * profession of the characters in the game.
 */
public class Guessing implements GameState {

  private final GameStateContext context;

  /**
   * Constructs a new Guessing state with the given game state context.
   *
   * @param context the context of the game state
   */
  public Guessing(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Checks if the clicked rectangle is a customer
   * and updates the game state accordingly.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    if (rectangleId.equals("officer") || rectangleId.equals("officer2")) {
      TextToSpeech.speak("Who is the thief?");
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation Dialog");
    alert.setHeaderText("Are you sure this is the thief?");
    alert.setContentText("You can only make one guess per game.");
    Optional<ButtonType> result = alert.showAndWait();

    if (result.get() != ButtonType.OK) {
      return;
    }

    RoomController roomController = context.getRoomController();
    roomController.stopTimeLine();
    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
    if (rectangleId.equals(context.getRectIdToGuess())) {
      Media media = new Media(getClass().getResource("/sounds/win.mp3").toString());
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      mediaPlayer.play();
      alert2.setTitle("Congratulations");
      alert2.setHeaderText("You won!");
      alert2.setContentText("You won! This is the thief!");
      alert2.showAndWait();
    } else {
      Media media = new Media(getClass().getResource("/sounds/lose.mp3").toString());
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      mediaPlayer.play();
      alert2.setTitle("Game Over");
      alert2.setHeaderText("You lost!");
      alert2.setContentText("This is the just a pedestrian.");
      alert2.showAndWait();
    }

    roomController.getBtnGuess().setText("Make a Guess");
    roomController.getBtnGuess().setDisable(true);

    context.setState(context.getGameOverState());
  }

  /**
   * Handles the event when the guess button is clicked. Since the player has already guessed, it
   * notifies the player.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {

    RoomController roomController = context.getRoomController();
    roomController.getBtnGuess().setText("Make a Guess");
    context.setState(context.getGameStartedState());
  }
}
