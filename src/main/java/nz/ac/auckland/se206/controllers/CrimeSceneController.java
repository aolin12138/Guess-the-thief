package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.ImageManager;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class CrimeSceneController {
  private static boolean isFirstTimeInit = true;

  private static GameStateContext context = new GameStateContext();
  private static double timeToCount = 360000;
  private static double timeToCountTo = 360000;
  private static double timeForGuessing = 60000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private MediaPlayer player;

  private static Timeline timeline = new Timeline();

  @FXML private Rectangle CCTVClue;
  @FXML private Rectangle phoneClue;
  @FXML private Rectangle newspaperClue;
  @FXML private Button btnGuess;
  @FXML private Button btnSlide;
  @FXML private StackPane indicatorPane;
  @FXML private Label timerLabel;
  @FXML private Rectangle suspect2Scene;
  @FXML private Rectangle suspect1Scene;
  @FXML private Rectangle suspect3Scene;

  @FXML private VBox imagesVBox;

  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;
  @FXML private ImageView brotherImage;
  @FXML private ImageView crimeImage;

  @FXML private Label crimeLabel;
  @FXML private Label workerLabel;
  @FXML private Label ownerLabel;
  @FXML private Label brotherLabel;

  public ImageManager currentImageManager;
  public ImageManager ownerImageManager;
  public ImageManager workerImageManager;
  public ImageManager brotherImageManager;
  public ImageManager crimeImageManager;
  public String id;

  @FXML
  public void initialize() {
    if (isFirstTimeInit) {}
    // context.setCrimeController(this); *******NEED THIS
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(50);
    // Timer label is updated here
    if (timeToCount % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
    }

    btnGuess
        .sceneProperty()
        .addListener(
            (observable, oldScene, newScene) -> {
              if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.sizeToScene();
              }
            });

    ownerImageManager = new ImageManager(ownerImage);
    workerImageManager = new ImageManager(workerImage);
    brotherImageManager = new ImageManager(brotherImage);
    crimeImageManager = new ImageManager(crimeImage);

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
                  if (timeToCount > timeForGuessing) {
                    timeToCount--;
                    progress =
                        (int)
                            (100
                                - (((timeToCountTo - timeForGuessing)
                                        - (timeToCount - timeForGuessing))
                                    * 100
                                    / (timeToCountTo - timeForGuessing)));
                  } else if ((timeToCount > 0)) {
                    // Here the timer has exceeded the time for investigation and the game must
                    // switch to the guess scene.
                    if (context.isAllSuspectsSpokenTo() && CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGuessingState());
                      try {
                        App.setRoot("guess");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                      // Stop the timer here, as once the suer switch to guessing state, they aren't
                      // coming back
                      timeline.stop();
                    } else if (!context.isAllSuspectsSpokenTo()
                        && CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not speak to every suspect during your investigation!\nWithout"
                              + " doing this, the investigation is incomplete!\n"
                              + "Click play again to replay.");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not find any clues in the crime scene!\n"
                              + "Finding clues is vital to conduting a good investigation!\n"
                              + "Click play again to replay");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    } else if (!context.isAllSuspectsSpokenTo()
                        && !CrimeSceneController.isAnyClueFound()) {
                      context.setState(context.getGameOverState());
                      GameOverController.setOutputText(
                          "You did not inspect the crime scene for clues or speak to every"
                              + " suspect!\n"
                              + "These steps are vital in any investigation.\n"
                              + "Click play again to replay.");
                      try {
                        App.setRoot("gamelost");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    }
                    // Once in guess state, player will never return to crime scene
                    timeline.stop();
                  }
                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount - timeForGuessing));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    // play an instruction sound when entering the room for the first time
    // Media media = new Media(getClass().getResource("/sounds/enter_room.mp3").toExternalForm());
    // MediaPlayer mediaPlayer = new MediaPlayer(media);
    // mediaPlayer.play();
    // isFirstTimeInit = false;
    // }}
  }

  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  public static void setProgress(int progressFromPreviousScene) {
    progress = progressFromPreviousScene;
  }

  public static void passTimeToSuspectScene(double timeToCount) {
    RoomController.setTimeToCount(timeToCount);
  }

  @FXML
  void onCCTVClueClicked(MouseEvent event) {
    context.clue1Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = phoneClue.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CCTV));
  }

  @FXML
  void onPhoneClueClicked(MouseEvent event) {
    context.clue2Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = phoneClue.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.PHONE));
  }

  @FXML
  void onNewspaperClueClicked(MouseEvent event) {
    context.clue3Found();
    // Satisfies requirement of at least one clue being discovered
    context.clueFound();
    Scene sceneOfButton = btnGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.NEWSPAPER));
  }

  @FXML
  void onGuessClick(ActionEvent event) throws IOException, URISyntaxException {
    // Check all 3 suspects have been spoken to and at least 1 clue has been clicked
    if (context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      // context.handleGuessClick();
      App.setRoot("guess");
    } else if (!context.isAllSuspectsSpokenTo() && isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/missing_suspect.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    } else if (context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/clue_reminder_1.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    } else if (!context.isAllSuspectsSpokenTo() && !isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/keep_investigating.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    }
  }

  @FXML
  void onSuspect1Clicked(MouseEvent event) throws IOException, ApiProxyException {
    Scene sceneOfButton = btnGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.ROOM));
    passTimeToSuspectScene(timeToCount);
  }

  @FXML
  void onSuspect2Clicked(MouseEvent event) throws IOException, ApiProxyException {
    Scene sceneOfButton = btnGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.ROOM));
    passTimeToSuspectScene(timeToCount);
  }

  @FXML
  void onSuspect3Clicked(MouseEvent event) throws IOException, ApiProxyException {
    Scene sceneOfButton = btnGuess.getScene();
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.ROOM));
    passTimeToSuspectScene(timeToCount);
  }

  public static boolean isAnyClueFound() {
    return context.isAnyClueFound();
  }

  @FXML
  public void styleScene() {

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

    btnSlide.setOnAction(event -> toggleHBox());
  }

  private void toggleHBox() {
    // Create the transition
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), imagesVBox);

    if (imagesVBox.isVisible()) {
      // Slide out
      transition.setToX(imagesVBox.getWidth() + 30); // Move off-screen
      transition.setOnFinished(event -> imagesVBox.setVisible(false)); // Hide after animation
    } else {
      // Slide in
      imagesVBox.setVisible(true); // Show before animation
      transition.setFromX(imagesVBox.getWidth() + 30); // Start off-screen
      transition.setToX(0); // Move to visible position
    }

    // Play the transition
    transition.play();
  }

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

    imagesVBox.setVisible(false);
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.ROOM));
  }

  public String getId() {
    return id;
  }

  public GameStateContext getContext() {
    return context;
  }
}
