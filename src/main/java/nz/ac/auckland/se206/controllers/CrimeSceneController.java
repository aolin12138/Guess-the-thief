package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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

public class CrimeSceneController {

  private static GameStateContext context = new GameStateContext();
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static Timeline timeline = new Timeline();

  /**
   * This method returns true if any clue is found
   *
   * @return
   */
  public static boolean isAnyClueFound() {
    return context.isAnyClueFound();
  }

  /**
   * This method is a setter that sets the context
   *
   * @param context
   */
  public static void setContext(GameStateContext context) {
    CrimeSceneController.context = context;
  }

  /**
   * This method is a getter that returns the context
   *
   * @return
   */
  public static GameStateContext getContext() {
    return context;
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

  private MediaPlayer player;
  private ImageManager ownerImageManager;
  private ImageManager workerImageManager;
  private ImageManager brotherImageManager;
  private ImageManager crimeImageManager;
  private String id;
  private ClueManager cameraImageManager;
  private ClueManager phoneImageManager;
  private ClueManager newspaperImageManager;

  private Media media =
      new Media(getClass().getResource("/sounds/new_intro_audio.mp3").toExternalForm());
  private MediaPlayer mediaPlayer = new MediaPlayer(media);

  /**
   * This method is called when the crime scene is loaded. It will set the timer and the progress
   * bar
   */
  @FXML
  public void initialize() {
    Platform.runLater(
        () -> {
          mediaPlayer.play();
        });
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

    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.45);
    ownerImage.setEffect(colorAdjust);
    workerImage.setEffect(colorAdjust);
    brotherImage.setEffect(colorAdjust);
    crimeImage.setEffect(colorAdjust);
    styleScene();

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  ringProgressIndicator.setProgress(TimelineManager.getProgress());
                  timerLabel.setText(Utils.formatTime(TimelineManager.getTimeToCount()));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /**
   * This method is called when the CCTV clue is clicked. It will take the user to the CCTV scene
   *
   * @param event
   */
  @FXML
  void onCameraClueClicked(MouseEvent event) {
    context.clue1Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = cameraImage.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CCTV));
  }

  /**
   * This method is called when the phone clue is clicked. It will take the user to the phone scene
   *
   * @param event
   */
  @FXML
  void onPhoneClueClicked(MouseEvent event) {
    context.clue2Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();

    Scene sceneOfButton = phoneImage.getScene();

    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.PHONE));
  }

  /**
   * This method is called when the newspaper clue is clicked. It will take the user to the
   * newspaper scene
   *
   * @param event
   */
  @FXML
  void onNewspaperClueClicked(MouseEvent event) {
    context.clue3Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = buttonGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.NEWSPAPER));
  }

  @FXML
  void onHelpButtonClicked(MouseEvent event) {
    showInstructionsButton.setVisible(false);
    hideInstructionsButton.setVisible(true);
    instructionsTextArea.setVisible(true);
    TranslateTransition instructionsTransition = new TranslateTransition();
    instructionsTransition.setNode(instructionsTextArea);
    instructionsTransition.setDuration(Duration.millis(500));
    instructionsTextArea.setVisible(true);
    instructionsTransition.setFromX(-500);
    instructionsTransition.setToX(4);
    instructionsTransition.play();
  }

  @FXML
  void onHideHelpClicked(MouseEvent event) {
    showInstructionsButton.setVisible(true);
    hideInstructionsButton.setVisible(false);

    TranslateTransition instructionsTransition = new TranslateTransition();
    instructionsTransition.setNode(instructionsTextArea);
    instructionsTransition.setDuration(Duration.millis(500));
    instructionsTransition.setFromX(4);
    instructionsTransition.setToX(-500);
    instructionsTransition.play();
  }

  /**
   * This method is called when the guess button is clicked. It will take the user to the guess
   * scene
   *
   * @param event
   * @throws IOException
   * @throws URISyntaxException
   */
  @FXML
  private void onGuessClick(ActionEvent event) throws IOException, URISyntaxException {
    // Check all 3 suspects have been spoken to and at least 1 clue has been clicked
    if (context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      Utils.setTimeUsed(TimelineManager.getTimeToCount());
      timeline.stop();
      context.setState(context.getGuessingState());
      // Play the guess scene
      App.setRoot("guess");
      // if the user has not spoken to all suspects and has not found any clues
    } else if (!context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Media sound =
          new Media(App.class.getResource("/sounds/missing_suspect.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
      // if the user has spoken to all suspects but has not found any clues
    } else if (context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Media sound =
          new Media(App.class.getResource("/sounds/clue_reminder_1.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
      // if the user has not spoken to all suspects and has not found any clues
    } else if (!context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      // Play the sound for the user to keep investigating
      Media sound =
          new Media(App.class.getResource("/sounds/keep_investigating.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    }
  }

  /** This method styles the scene */
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

  /** This method toggles the VBox */
  private void toggleVerticalBox() {
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
   * @param event
   * @throws IOException
   * @throws InterruptedException
   */
  @FXML
  public void handleImageClick(MouseEvent event) throws IOException, InterruptedException {
    ImageView clickedImage = (ImageView) event.getSource();
    id = clickedImage.getId();

    ImageView imageView = (ImageView) event.getSource();
    Scene sceneOfButton = imageView.getScene();

    RoomController roomController = SceneManager.getRoomLoader().getController();
    roomController.setContext(context);
    context.setRoomController(roomController);

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
   * This method is a getter that returns the id
   *
   * @return
   */
  public String getId() {
    return id;
  }
}
