package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class StartController {

  @FXML Button startButton;

  Media media = new Media(getClass().getResource("/sounds/opening_voice.mp3").toExternalForm());
  MediaPlayer mediaPlayer = new MediaPlayer(media);

  @FXML
  public void initialize() {
    Platform.runLater(
        () -> {
          mediaPlayer.play();
          mediaPlayer.setOnEndOfMedia(
              () -> {
                startButton.setDisable(false);
              });
        });
  }

  @FXML
  public void onEnterPressed() {
    try {

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/room.fxml"));
      Parent root = loader.load();
      // RoomController controller = loader.getController();
      // controller.getContext().setRoomController(controller);
      startButton.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }
}
