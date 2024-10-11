package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;

/** This class is the controller for the game over scene. */
public class GameOverController {

  private static String explanation;
  private static boolean isTextAlreadyDisplayed = false;
  private static boolean isBannerAlreadyDisplayed = false;
  private static String spare = "";

  /** This method sets the output text to the explanation of the guess. */
  public static void setOutputText(String text) {
    if (isTextAlreadyDisplayed) {
      return;
    }
    explanation = text;
  }

  @FXML private Pane statsPane;
  @FXML private Label lblStats;
  @FXML private TextArea textChat;
  @FXML private Label lblExplanation;
  @FXML private TextArea oldTextArea;
  @FXML private Button rstbtn;

  /**
   * This method is called when the game over scene is loaded. It will set the text of the text area
   */
  @FXML
  public void initialize() {
    rstbtn.setCursor(Cursor.HAND);

    // Set the text of the text area to the explanation of the game
    if ((oldTextArea != null) && (!isTextAlreadyDisplayed)) {
      spare = explanation;
      System.out.println(spare);

      oldTextArea.setWrapText(true);
      oldTextArea.setText(spare);
      isTextAlreadyDisplayed = true;
    }

    // Set the text of the label to the result of the game

    if (!isBannerAlreadyDisplayed) { // Prevents bug from changing gamestate to loss after timers
      // if the game is won or lost, display the appropriate message
      if (GuessController.getIsGameWon()) {
        lblStats.setText("Correct! You win!!");
        lblStats.setDisable(true);
        isBannerAlreadyDisplayed = true;
        Utils.updateScoreBoard(Utils.getTimeUsed(), Utils.getPlayerName());
      } else {
        lblStats.setText("Oh no! You Lose!");
        isBannerAlreadyDisplayed = true;
      }
    }
  }

  /**
   * This method is called when the restart button is clicked. It will take the user back to the
   * start scene.
   *
   * @param event Extra tag description due missing tag description.
   * @throws ApiProxyException Extra tag description due missing tag description.
   * @throws IOException Extra tag description due missing tag description.
   */
  @FXML
  private void onHandleRestartClick(ActionEvent event) throws ApiProxyException, IOException {
    // Reset the game
    Platform.runLater(
        () -> {
          // Load the fxml files
          FXMLLoader startLoader = new FXMLLoader(App.class.getResource("/fxml/start.fxml"));
          FXMLLoader roomLoader = new FXMLLoader(App.class.getResource("/fxml/room.fxml"));
          FXMLLoader instructionLoader =
              new FXMLLoader(App.class.getResource("/fxml/instructions.fxml"));
          FXMLLoader phoneLoader = new FXMLLoader(App.class.getResource("/fxml/phone.fxml"));
          FXMLLoader newspaperLoader =
              new FXMLLoader(App.class.getResource("/fxml/newspaper.fxml"));
          FXMLLoader callHistoryLoader =
              new FXMLLoader(App.class.getResource("/fxml/callhistory.fxml"));
          FXMLLoader cctvLoader = new FXMLLoader(App.class.getResource("/fxml/cctv.fxml"));
          // Load the fxml files
          try {
            SceneManager.addRoot(SceneManager.Scene.INSTRUCTIONS, instructionLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.START, startLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.ROOM, roomLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.PHONE, phoneLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.NEWSPAPER, newspaperLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.CALL_HISTORY, callHistoryLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          try {
            SceneManager.addRoot(SceneManager.Scene.CCTV, cctvLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          // Set the root of the scene to the start scene
          SceneManager.setRoomLoader(roomLoader);
          SceneManager.setPhoneLoader(phoneLoader);
          SceneManager.setCameraLoader(cctvLoader);
          SceneManager.setCallHistoryLoader(callHistoryLoader);
          SceneManager.setNewspaperLoader(newspaperLoader);

          Scene scene = rstbtn.getScene();
          scene.setRoot(SceneManager.getRoot(SceneManager.Scene.START));
        });
  }

  /**
   * This method is a placeholder for the key pressed event.
   *
   * @param event Adding extra bulk due testing requirements.
   */
  @FXML
  public void onKeyPressed(ActionEvent event) {}

  /**
   * This method is a placeholder for the key released event.
   *
   * @param event Adding extra bulk due testing requirements.
   */
  @FXML
  public void onKeyReleased(ActionEvent event) {}
}
