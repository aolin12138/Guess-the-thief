package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ClueManager;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.ImageManager;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

/**
 * Handles the action when the guess button is clicked.
 *
 * <p>This method processes the user's guess, checks if the guess is correct, and updates the game
 * state accordingly.
 *
 * @param event the mouse event that triggered this method
 */
public class CrimeSceneController {

  private static GameStateContext context = new GameStateContext();
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();
  private static boolean switchedRing = false;
  private static boolean initialisedRing = true;

  /**
   * This method is a getter that returns the context.
   *
   * @return returns the context of the game state.
   */
  public static boolean isAnyClueFound() {
    return context.isAnyClueFound();
  }

  /**
   * This method is a setter that sets the context.
   *
   * @param context Adding more words due to requirements for description.
   */
  public static void setContext(GameStateContext context) {
    CrimeSceneController.context = context;
  }

  /**
   * This method is a getter that returns the context.
   *
   * @return returns the context of the game state.
   */
  public static GameStateContext getContext() {
    return context;
  }

  public static void resetBooleans() {
    switchedRing = false;
    initialisedRing = false;
  }

  @FXML private Rectangle phoneClue;
  @FXML private Rectangle newspaperClue;
  @FXML private Button buttonGuess;
  @FXML private Button buttonSlide;
  @FXML private Button showInstructionsButton;
  @FXML private Button hideInstructionsButton;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Rectangle suspect2Scene;
  @FXML private Rectangle suspect1Scene;
  @FXML private Rectangle suspect3Scene;

  @FXML private VBox imagesVerticalBox;

  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;
  @FXML private ImageView brotherImage;
  @FXML private ImageView crimeImage;
  @FXML private ImageView cameraImage;
  @FXML private ImageView phoneImage;
  @FXML private ImageView newspaperImage;

  @FXML private Label crimeLabel;
  @FXML private Label workerLabel;
  @FXML private Label ownerLabel;
  @FXML private Label brotherLabel;

  @FXML private TextArea instructionsTextArea;

  private ImageManager ownerImageManager;
  private ImageManager workerImageManager;
  private ImageManager brotherImageManager;
  private ImageManager crimeImageManager;
  private String id;
  private ClueManager cameraImageManager;
  private ClueManager phoneImageManager;
  private ClueManager newspaperImageManager;

  /**
   * This method is called when the crime scene is loaded. It will set the timer and the progress
   * bar
   *
   * @throws URISyntaxException Adding more words due to requirements for description.
   */
  @FXML
  public void initialize() throws URISyntaxException {
    Utils.playSoundtrack("new_intro_audio.mp3");
    // Add the ring progress indicator to the pane
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);

    buttonGuess
        .sceneProperty()
        .addListener(
            (observable, oldScene, newScene) -> {
              if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.sizeToScene();
              }
            });

    buttonSlide.setCursor(Cursor.HAND);
    buttonGuess.setCursor(Cursor.HAND);
    showInstructionsButton.setCursor(Cursor.HAND);
    hideInstructionsButton.setCursor(Cursor.HAND);
    hideInstructionsButton.setVisible(false);
    instructionsTextArea.setVisible(false);

    ownerImageManager = new ImageManager(ownerImage);
    workerImageManager = new ImageManager(workerImage);
    brotherImageManager = new ImageManager(brotherImage);
    crimeImageManager = new ImageManager(crimeImage);
    cameraImageManager = new ClueManager(cameraImage);
    phoneImageManager = new ClueManager(phoneImage);
    newspaperImageManager = new ClueManager(newspaperImage);
    // Set the brightness of the images
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.45);
    ownerImage.setEffect(colorAdjust);
    workerImage.setEffect(colorAdjust);
    brotherImage.setEffect(colorAdjust);
    crimeImage.setEffect(colorAdjust);
    styleScene();
    // set the progress bar and timer
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  ringProgressIndicator.setProgress(TimelineManager.getProgress());
                  timerLabel.setText(Utils.formatTime(TimelineManager.getTimeToCount()));
                  if (TimelineManager.getTimeToCount() > 60000 && !initialisedRing) {
                    setGreenRing();
                  }
                  if (TimelineManager.getTimeToCount() < 60000 && !switchedRing) {
                    setRedRing();
                  }
                  // flash the timer red below 30 seconds
                  if (TimelineManager.getTimeToCount() <= 30000) {
                    if ((int) (TimelineManager.getTimeToCount() / 1000) % 2 != 0) {
                      timerLabel.setStyle("-fx-text-fill: rgba(255,0,0,1);");
                    } else {
                      timerLabel.setStyle("-fx-text-fill: rgba(142,3,3,1);");
                    }
                  }
                }));
    // Set the cycle count to indefinite
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /**
   * This method is called when the CCTV clue is clicked. It will take the user to the CCTV scene.
   *
   * @param event Adding more words due to requirements for description.
   */
  @FXML
  void onCameraClueClicked(MouseEvent event) {
    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(event);
    }
    context.clue1Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = cameraImage.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CCTV));
  }

  /**
   * Handles the action when the phone clue is clicked.
   *
   * <p>This method hides the instructions if they are visible, marks the second clue as found, and
   * changes the scene to the phone scene.
   *
   * @param event the mouse event that triggered this method
   */
  @FXML
  void onPhoneClueClicked(MouseEvent event) {
    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(event);
    }
    context.clue2Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();

    Scene sceneOfButton = phoneImage.getScene();

    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.PHONE));
  }

  /**
   * Handles the action when the newspaper clue is clicked.
   *
   * <p>This method hides the instructions if they are visible, marks the first clue as found, and
   * changes the scene to the newspaper scene.
   *
   * @param event the mouse event that triggered this method
   */
  @FXML
  void onNewspaperClueClicked(MouseEvent event) {
    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(event);
    }
    context.clue3Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = buttonGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.NEWSPAPER));
  }

  /**
   * Handles the action when the help button is clicked.
   *
   * <p>This method hides the show instructions button, makes the instructions text area visible,
   * and animates the instructions text area sliding in from the left.
   *
   * @param event the mouse event that triggered this method
   */
  @FXML
  // This methoid is called when the user presses the help button, and the instructions will appear.
  void onHelpButtonClicked(MouseEvent event) {
    showInstructionsButton.setVisible(false);
    instructionsTextArea.setVisible(true);
    TranslateTransition instructionsTransition = new TranslateTransition();
    instructionsTransition.setNode(instructionsTextArea);
    instructionsTransition.setDuration(Duration.millis(100));
    instructionsTransition.setFromX(0);
    instructionsTransition.setToX(460);
    instructionsTransition.play();
    instructionsTransition.setOnFinished(e -> hideInstructionsButton.setVisible(true));
  }

  /**
   * Handles the action when the hide help button is clicked.
   *
   * <p>This method hides the hide instructions button, makes the instructions text area slide out
   * to the left, and makes the show instructions button visible once the animation is finished.
   *
   * @param event the mouse event that triggered this method
   */
  @FXML
  void onHideHelpClicked(MouseEvent event) {
    // This method is called when the user hides the instructions or attempts to click any other
    // point on the game.
    hideInstructionsButton.setVisible(false);
    TranslateTransition instructionsTransition = new TranslateTransition();
    instructionsTransition.setNode(instructionsTextArea);
    instructionsTransition.setDuration(Duration.millis(100));
    instructionsTransition.setFromX(460);
    instructionsTransition.setToX(0);
    instructionsTransition.play();
    instructionsTransition.setOnFinished(e -> showInstructionsButton.setVisible(true));
  }

  /**
   * This method is called when the guess button is clicked. It will take the user to the guess
   * scene.
   *
   * @param event Adding more words due to requirements for description.
   * @throws IOException Adding more words due to requirements for description.
   * @throws URISyntaxException Adding more words due to requirements for description.
   */
  @FXML
  private void onGuessClick(ActionEvent event) throws IOException, URISyntaxException {
    // need to hide the instructions if it is visible
    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(null);
    }

    // Check all 3 suspects have been spoken to and at least 1 clue has been clicked
    if (context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      Utils.setTimeUsed(TimelineManager.getTimeToCount());
      TimelineManager.stopTimer();
      context.setState(context.getGuessingState());
      // Play the guess scene
      App.setRoot("guess");
      // if the user has not spoken to all suspects and has not found any clues
    } else if (!context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Utils.stopPlayer();
      Utils.playSoundtrack("missing_suspect.mp3");
      return;
      // if the user has spoken to all suspects but has not found any clues
    } else if (context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Utils.stopPlayer();
      Utils.playSoundtrack("clue_reminder_1.mp3");
      return;
      // if the user has not spoken to all suspects and has not found any clues
    } else if (!context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Utils.stopPlayer();
      Utils.playSoundtrack("keep_investigating.mp3");
      return;
    }
  }

  /** This method styles the scene. */
  @FXML
  public void styleScene() {

    // Makes the owner image hoverable
    ownerImage.setOnMouseEntered(
        e -> {
          ownerImageManager.hoverIn();
          ownerLabel.setVisible(true);
        });
    ownerImage.setOnMouseExited(
        e -> {
          ownerImageManager.hoverOut();
          ownerLabel.setVisible(false);
        });
    // Makes the worker image hoverable
    workerImage.setOnMouseEntered(
        e -> {
          workerImageManager.hoverIn();
          workerLabel.setVisible(true);
        });
    workerImage.setOnMouseExited(
        e -> {
          workerImageManager.hoverOut();
          workerLabel.setVisible(false);
        });
    // Makes the brother image hoverable
    brotherImage.setOnMouseEntered(
        e -> {
          brotherImageManager.hoverIn();
          brotherLabel.setVisible(true);
        });
    brotherImage.setOnMouseExited(
        e -> {
          brotherImageManager.hoverOut();
          brotherLabel.setVisible(false);
        });
    // Makes the crime image hoverable
    crimeImage.setOnMouseEntered(
        e -> {
          crimeImageManager.hoverIn();
          crimeLabel.setVisible(true);
        });
    crimeImage.setOnMouseExited(
        e -> {
          crimeImageManager.hoverOut();
          crimeLabel.setVisible(false);
        });
    // Makes the camera image hoverable
    cameraImage.setOnMouseEntered(
        e -> {
          cameraImageManager.hoverIn();
        });
    cameraImage.setOnMouseExited(
        e -> {
          cameraImageManager.hoverOut();
        });
    // Makes the phone image hoverable
    phoneImage.setOnMouseEntered(
        e -> {
          phoneImageManager.hoverIn();
        });
    phoneImage.setOnMouseExited(
        e -> {
          phoneImageManager.hoverOut();
        });
    // Makes the newspaper image hoverable
    newspaperImage.setOnMouseEntered(
        e -> {
          newspaperImageManager.hoverIn();
        });
    newspaperImage.setOnMouseExited(
        e -> {
          newspaperImageManager.hoverOut();
        });
    // Set handler for the slide button
    buttonSlide.setOnAction(e -> toggleVerticalBox());
  }

  /** This method toggles the VBox. */
  private void toggleVerticalBox() {

    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(null);
    }
    // Create the transition
    TranslateTransition transition =
        new TranslateTransition(Duration.seconds(0.5), imagesVerticalBox);

    if (imagesVerticalBox.isVisible()) {
      // Slide out
      transition.setToX(imagesVerticalBox.getWidth() + 30);
      transition.setOnFinished(event -> imagesVerticalBox.setVisible(false));
      buttonSlide.setText("Show Side Bar");
    } else {
      // Slide in
      imagesVerticalBox.setVisible(true);
      transition.setFromX(imagesVerticalBox.getWidth() + 30);
      transition.setToX(0);
      buttonSlide.setText("Hide Side Bar");
    }

    // Play the transition
    transition.play();
  }

  /**
   * This method is called when an image is clicked. It will set the context and take the user to
   * the image scene
   *
   * @param event the mouse event that triggered this method
   * @throws IOException if the image cannot be loaded throws an IOException
   * @throws InterruptedException if the thread is interrupted throws an InterruptedException
   */
  @FXML
  public void handleImageClick(MouseEvent event) throws IOException, InterruptedException {
    if (hideInstructionsButton.isVisible()) {
      onHideHelpClicked(event);
    }
    buttonSlide.setText("Show Side Bar");
    ImageView clickedImage = (ImageView) event.getSource();
    id = clickedImage.getId();
    // get the source of the event
    ImageView imageView = (ImageView) event.getSource();
    Scene sceneOfButton = imageView.getScene();
    // get the room controller
    RoomController roomController = SceneManager.getRoomLoader().getController();
    roomController.setContext(context);
    context.setRoomController(roomController);
    // set the person image
    SceneManager.getRoot(SceneManager.Scene.ROOM)
        .sceneProperty()
        .addListener(
            (obs, oldScene, newScene) -> {
              if (newScene != null) {
                try {
                  roomController.setPersonImage(event, id);
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                } // Call the method only when entering root2
              }
            });
    imagesVerticalBox.setVisible(false);
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.ROOM));
  }

  /**
   * This method is a getter that returns the id.
   *
   * @return Adding extra bulk due testing requirements.
   */
  public String getId() {
    return id;
  }

  /** Sets the ring progress indicator to red. */
  public void setRedRing() {
    indicatorPane.getChildren().remove(ringProgressIndicator);
    ringProgressIndicator = new RingProgressIndicator(true);
    ringProgressIndicator.setRingWidth(50);
    indicatorPane.getChildren().add(ringProgressIndicator);
    timerLabel.setStyle("-fx-text-fill: rgba(255,0,0,1);");
    switchedRing = true;
  }

  /** Sets the ring progress indicator to green. */
  public void setGreenRing() {
    indicatorPane.getChildren().remove(ringProgressIndicator);
    ringProgressIndicator = new RingProgressIndicator();
    ringProgressIndicator.setRingWidth(50);
    indicatorPane.getChildren().add(ringProgressIndicator);
    timerLabel.setStyle("-fx-text-fill: #83F28F;");
    initialisedRing = true;
  }
}
