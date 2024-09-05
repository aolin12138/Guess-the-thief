package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;

public class InstructionsController {

  @FXML private Button backButton;

  @FXML
  void onGoBackButtonPressed(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.START));
    // App.setRoot("start"); Creates a new instance of the start scene every time, which means any
    // intro music will play again
  }
}
