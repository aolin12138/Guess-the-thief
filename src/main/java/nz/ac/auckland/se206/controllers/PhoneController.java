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
  @FXML private Rectangle callRectangle;
  @FXML private ImageView lockScreen;
  @FXML private ImageView screen;
  @FXML private ImageView callHistory;
  @FXML private StackPane phonePane;

  private double initialY;

  @FXML
  public void initialize() {

    Rectangle clip = new Rectangle(209, 400); // Clip rectangle matching the phone screen size
    phonePane.setClip(clip);

    lockScreen.setOnMousePressed(
        event -> {
          initialY = event.getSceneY(); // Store the initial Y position
        });

    lockScreen.setOnMouseDragged(
        event -> {
          double currentY = event.getSceneY(); // Get the current Y position during the drag
          double dragDistance = initialY - currentY; // Calculate how far the user has dragged

          // Only move the lock screen if the drag distance is significant
          if (dragDistance > 40) { // Threshold to avoid triggering on small movements
            unlock(lockScreen, phonePane.getHeight());
            callRectangle.setDisable(false);
          }
        });
  }

  @FXML
  private void onCallClicked(MouseEvent event) {
    callHistory.setVisible(true);
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
