package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

/** This class is the controller for the newspaper scene. */
public class NewspaperController {
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  @FXML private StackPane indicatorPane;
  @FXML private Button nextPageButton;
  @FXML private Button previousPageButton;
  @FXML private Button returnButton;
  @FXML private Label timerLabel;

  @FXML private ImageView newspaper1;
  @FXML private ImageView newspaper2;
  @FXML private ImageView newspaper3;

  /**
   * This method is called when the next page button is clicked. It will take the user to the next
   * page
   *
   * @param event extra tag description due missing tag description.
   */
  @FXML
  private void onNextPage(ActionEvent event) {

    // If the first newspaper is visible, hide it and show the second newspaper
    if (newspaper1.isVisible()) {
      newspaper1.setVisible(false);
      newspaper2.setVisible(true);
      previousPageButton.setDisable(false);
      nextPageButton.setDisable(false);
      // If the second newspaper is visible, hide it and show the third newspaper
    } else if (newspaper2.isVisible()) {
      newspaper2.setVisible(false);
      newspaper3.setVisible(true);
      previousPageButton.setDisable(false);
      nextPageButton.setDisable(true);
      // If the third newspaper is visible, hide it and show the first newspaper
    } else {
      newspaper3.setVisible(true);
      nextPageButton.setDisable(true);
      previousPageButton.setDisable(false);
    }
  }

  /**
   * This method is called when the previous page button is clicked. It will take the user to the
   * previous page.
   *
   * @param event extra tag description due missing tag.
   */
  @FXML
  private void onPreviousPage(ActionEvent event) {
    // If the third newspaper is visible, hide it and show the second newspaper
    if (newspaper3.isVisible()) {
      newspaper3.setVisible(false);
      newspaper2.setVisible(true);
      nextPageButton.setDisable(false);
      previousPageButton.setDisable(false);
      // If the second newspaper is visible, hide it and show the first newspaper
    } else if (newspaper2.isVisible()) {
      newspaper2.setVisible(false);
      newspaper1.setVisible(true);
      nextPageButton.setDisable(false);
      previousPageButton.setDisable(true);
      // If the first newspaper is visible, hide it and show the third newspaper
    } else {
      newspaper1.setVisible(true);
      previousPageButton.setDisable(true);
      nextPageButton.setDisable(false);
    }
  }

  /**
   * This method is called when the return button is clicked. It will take the user back to the
   * crime scene
   *
   * @param event extra tag description due missing tag
   */
  @FXML
  private void onReturnToCrimeScene(ActionEvent event) {
    Scene sceneOfButton = returnButton.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
  }

  /** This method initializes the newspaper scene. */
  @FXML
  public void initialize() {
    // Set the first newspaper to be visible and the other two to be invisible
    newspaper1.setVisible(true);
    newspaper2.setVisible(false);
    newspaper3.setVisible(false);
    previousPageButton.setDisable(true);

    previousPageButton.setCursor(Cursor.HAND);
    returnButton.setCursor(Cursor.HAND);
    nextPageButton.setCursor(Cursor.HAND);

    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  ringProgressIndicator.setProgress(TimelineManager.getProgress());
                  timerLabel.setText(Utils.formatTime(TimelineManager.getTimeToCount()));
                  // flash the timer red below 30 seconds
                  if (TimelineManager.getTimeToCount() <= 30000) {
                    if ((int) (TimelineManager.getTimeToCount() / 1000) % 2 == 0) {
                      timerLabel.setStyle("-fx-text-fill: rgba(255,0,0,1);");
                    } else {
                      timerLabel.setStyle("-fx-text-fill: rgba(142,3,3,1);");
                    }
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
