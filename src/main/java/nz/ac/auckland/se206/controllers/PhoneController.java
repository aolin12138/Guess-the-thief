package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.SceneManager;

public class PhoneController {

  @FXML private Button BackBtn;
  @FXML private Rectangle PhoneAppRectangle;

  @FXML
  void onPhoneAppClicked(MouseEvent event) {
    Scene sceneOfButton = PhoneAppRectangle.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CALL_HISTORY));
  }

  @FXML
  void onReturnToCrimeScene(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }
}
