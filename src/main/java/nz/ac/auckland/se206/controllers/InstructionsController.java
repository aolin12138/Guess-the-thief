package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class InstructionsController {

  @FXML private Button backButton;

  @FXML
  public void onGoBackButtonPressed(ActionEvent event) {
    try {

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/room.fxml"));
      Parent root = loader.load();
      // RoomController controller = loader.getController();
      // controller.getContext().setRoomController(controller);
      backButton.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
