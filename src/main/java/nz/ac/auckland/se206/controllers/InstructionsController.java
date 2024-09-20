package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;

public class InstructionsController {

  @FXML private Button backButton;

  /**
   * This method is called when the go back button is clicked. It will take the user back to the
   * start scene.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onGoBackButtonPressed(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.START));
    // intro music will play again
  }
}
