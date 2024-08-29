package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The GameOver state of the game. Handles interactions after the game has ended, informing the
 * player that the game is over and no further actions can be taken.
 */
public class GameOver implements GameState {

  private final GameStateContext context;

  /**
   * Constructs a new GameOver state with the given game state context.
   *
   * @param context the context of the game state
   */
  public GameOver(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Informs the player that the game is over and
   * provides the profession of the clicked character if applicable.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    if (rectangleId.equals("rectCashier") || rectangleId.equals("rectWaitress")) {
      return;
    }
    String clickedProfession = context.getPerson(rectangleId).getRole();
    TextToSpeech.speak("Game Over, you have already guessed! This is the " + clickedProfession);
  }

  /**
   * Handles the event when the guess button is clicked. Informs the player that the game is over
   * and no further guesses can be made.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("Game Over");
    alert.setContentText("You have already guessed! The game is over.");
    alert.showAndWait();
  }

  @Override
  public void handleTrashBinClick(MouseEvent event, String itemId) throws IOException {
    // Do nothing
  }

  @Override
  public void handleCameraClick(MouseEvent event, String itemId) throws IOException {
    // Do nothing
  }

  @Override
  public void handleCarClick(MouseEvent event, String itemId) throws IOException {
    // Do nothing
  }
}
