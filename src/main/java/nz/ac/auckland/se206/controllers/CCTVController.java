package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;

public class CCTVController {

  @FXML private Button ReturnButton;

  @FXML
  void onReturnButtonClicked(ActionEvent event) {
    Scene sceneOfButton = ReturnButton.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }
}
