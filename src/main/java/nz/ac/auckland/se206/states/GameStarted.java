package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.controllers.RoomController;

/**
 * The GameStarted state of the game. Handles the initial interactions when the game starts,
 * allowing the player to chat with characters and prepare to make a guess.
 */
public class GameStarted implements GameState {

  private final GameStateContext context;

  /**
   * Constructs a new GameStarted state with the given game state context.
   *
   * @param context the context of the game state
   */
  public GameStarted(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Depending on the clicked rectangle, it either
   * provides an introduction or transitions to the chat view.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {

    context.getRoomController().noTalking();
    Task<Void> task =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            context.getRoomController().setPerson(context.getPerson(rectangleId));
            return null;
          }
        };

    context.getRoomController().talked();
    Thread backgrounThread = new Thread(task);
    backgrounThread.start();
  }

  /**
   * Handles the event when the guess button is clicked. Prompts the player to make a guess and
   * transitions to the guessing state.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {
    if (!context.getRoomController().getHasTalked()) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Information Dialog");
      alert.setHeaderText("Talk to at least one suspect before making a guess.");
      alert.showAndWait();
      return;
    } else if (!context.getRoomController().isWalletFound()
        && !context.getRoomController().isCameraFound()
        && !context.getRoomController().isDashcamFound()) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Information Dialog");
      alert.setHeaderText("There is missing evidence.");
      alert.showAndWait();
      return;
    }

    Media media = new Media(getClass().getResource("/sounds/make_guess.mp3").toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();
    RoomController roomController = context.getRoomController();
    roomController.getBtnGuess().setText("Rethink");
    context.setState(context.getGuessingState());
  }
}
