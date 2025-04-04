package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;

/** This class is the controller for the instructions scene. */
public class InstructionsController {

  /**
   * This method is called when the go back button is clicked. It will take the user back to the
   * start scene.
   */
  @FXML private Button backButton;

  /**
   * This method is called when the go back button is clicked. It will take the user back to the
   * start scene.
   *
   * @param event extra tag description due missing tag description.
   * @throws IOException extra tag description due missing tag description.
   */
  @FXML
  private void onGoBackButtonPressed(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.START));
    // intro music will play again
  }
}
