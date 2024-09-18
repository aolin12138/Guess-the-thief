package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;

public class NewspaperController {

  @FXML private Button NextPageBtn;
  @FXML private Button PreviousPageBtn;
  @FXML private Button ReturnBtn;

  @FXML
  void onNextPage(ActionEvent event) {}

  @FXML
  void onPreviousPage(ActionEvent event) {}

  @FXML
  void onReturnToCrimeScene(ActionEvent event) {
    Scene sceneOfButton = ReturnBtn.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }
}
