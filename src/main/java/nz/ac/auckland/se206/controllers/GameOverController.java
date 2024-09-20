package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class GameOverController {

  private static String explanation;
  private static boolean isTextAlreadyDisplayed = false;
  private static boolean isBannerAlreadyDisplayed = false;

  /** This method sets the output text to the explanation of the guess. */
  public static void setOutputText(String text) {
    explanation = text;
  }

  @FXML private Pane statsPane;
  @FXML private Label lblStats;
  @FXML private TextArea textChat;
  @FXML private Label lblExplanation;
  @FXML private TextArea textArea;

  /**
   * This method is called when the game over scene is loaded. It will set the text of the text area
   */
  @FXML
  public void initialize() {

    // Set the text of the text area to the explanation of the game
    if ((textArea != null) && !isTextAlreadyDisplayed) {

      textArea.setWrapText(true);
      textArea.setText(explanation);
      textArea.setDisable(true);
      isTextAlreadyDisplayed = true;
    }

    // Set the text of the label to the result of the game

    if (!isBannerAlreadyDisplayed) { // Prevents bug from changing gamestate to loss after timers
      // run out
      if (GuessController.getIsGameWon()) {
        lblStats.setText("Correct! You win!!");
        lblStats.setDisable(true);
      } else {
        lblStats.setText("Oh no! You Lose!");
      }
    }
  }

  /**
   * This method is called when the restart button is clicked. It will take the user back to the
   * start scene.
   *
   * @param event
   * @throws ApiProxyException
   * @throws IOException
   */
  @FXML
  public void handleRestartClick(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("start");
  }

  /**
   * This method is a placeholder for the key pressed event.
   *
   * @param event
   */
  @FXML
  public void onKeyPressed(ActionEvent event) {}

  /**
   * This method is a placeholder for the key released event.
   *
   * @param event
   */
  @FXML
  public void onKeyReleased(ActionEvent event) {}
}
