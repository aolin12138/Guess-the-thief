package nz.ac.auckland.se206.controllers;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;

public class PhoneController {

  @FXML private Button BackBtn;
  @FXML private Rectangle PhoneAppRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private StackPane phonePane;

  @FXML
  public void initialize() {

    Rectangle clip = new Rectangle(209, 400); // Clip rectangle matching the phone screen size
    phonePane.setClip(clip);
    lockScreen.setOnMouseDragged(
        event -> {
          double dragY = event.getSceneY();

          // Unlock threshold: swipe upwards (Y value less than half of the screen height)
          if (dragY > phonePane.getHeight() / 2) {
            unlock(lockScreen, phonePane.getHeight());
          }
        });
  }

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

  private void unlock(ImageView lockScreen, double height) {
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), lockScreen);
    transition.setToY(-height); // Move the lock screen off the top
    transition.setOnFinished(event -> lockScreen.setVisible(false)); // Hide lock screen
    transition.play();
  }
}
