package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.ImageManager;
import nz.ac.auckland.se206.Person;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {
  private static boolean isFirstTimeInit = true;
  private static boolean hasTalked = false;
  private static boolean walletFound = false;
  private static boolean cameraFound = false;
  private static boolean dashcamFound = false;
  private static boolean isCarFound = false;
  private static GameStateContext context = new GameStateContext();
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();

  /**
   * Sets the time to count down from.
   *
   * @param timeFromPreviousScene
   */
  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  /**
   * Passes the time to the crime scene controller.
   *
   * @param timeToCount
   */
  public static void passTimeToCrimeScene(double timeToCount) {
    CrimeSceneController.setTimeToCount(timeToCount);
  }

  private MediaPlayer player;

  @FXML private Rectangle rectPerson1;
  @FXML private Rectangle rectPerson2;
  @FXML private Rectangle rectPerson3;
  @FXML private Rectangle officer;
  @FXML private Rectangle officer2;
  @FXML private Rectangle camera;
  @FXML private Rectangle dashcam;
  @FXML private Rectangle car;

  @FXML private Ellipse trashBin;

  @FXML private Label lblProfession;
  @FXML private Label timerLabel;
  @FXML private Label chatStats;
  @FXML private Label crimeLabel;
  @FXML private Label workerLabel;
  @FXML private Label ownerLabel;
  @FXML private Label brotherLabel;

  @FXML private Button buttonGuess;
  @FXML private Button buttonSend;
  @FXML private Button buttonBack;
  @FXML private Button buttonSlide;

  @FXML private TextArea textaChat;

  @FXML private TextField textInput;

  @FXML private ImageView carImage;
  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;
  @FXML private ImageView brotherImage;
  @FXML private ImageView crimeImage;
  @FXML private ImageView displayImage;

  @FXML private StackPane indicatorPane;
  @FXML private Pane statsPane;

  @FXML private VBox imagesVerticalBox;

  private ChatCompletionRequest chatCompletionRequest;
  private Person person;
  private ImageView currentImage = null;

  private ImageManager currentImageManager;
  private ImageManager ownerImageManager;
  private ImageManager workerImageManager;
  private ImageManager brotherImageManager;
  private ImageManager crimeImageManager;
  private Scene suspectScene;

  private Timeline timeline = new Timeline();

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    // Probably delete this since we will only load this scene once
    if (isFirstTimeInit) {
      isFirstTimeInit = false;
    }
    textInput.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");

    buttonSend
        .sceneProperty()
        .addListener(
            (observable, oldScene, newScene) -> {
              if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.sizeToScene();
              }
              if (newScene != null) {
                newScene.addEventHandler(
                    KeyEvent.KEY_PRESSED,
                    event -> {
                      if (event.getCode() == KeyCode.ENTER) {
                        try {
                          onSendMessage(new ActionEvent());
                        } catch (ApiProxyException | IOException e) {
                          e.printStackTrace();
                        }
                      }
                    });
              }
            });

    // Set the images of the scene switches
    currentImageManager = new ImageManager(currentImage);
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

    context.setRoomController(this);
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(60);
    // Timer label is updated here
    if (timeToCount % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeToCount));
    }

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeToCount > 0) {
                    timeToCount--;
                    progress = (int) (100 - ((timeToCountTo - timeToCount) * 100 / timeToCountTo));
                  } else {
                    Utils.checkConditions(
                        context,
                        context.isAllSuspectsSpokenTo(),
                        CrimeSceneController.isAnyClueFound(),
                        timeline);
                    // // Program switch to guess scene here ONLY if clues and suspects have been
                    // // correctly interacted with
                    // // Before switching state, make sure the game is still in the game started
                    // state
                    // // and that we havent already switched state. Otherwise it will cause a bug
                    // if (!(context.getGameState().equals(context.getGameStartedState()))) {
                    //   System.out.println("hello g " + context.getGameState());
                    //   timeline.stop();
                    //   return;
                    // }
                    // if (context.isAllSuspectsSpokenTo()
                    //     && CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGuessingState());
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("guess");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    //   // Stop the timer here, as once the suer switch to guessing state, they
                    // aren't
                    //   // coming back
                    //   timeline.stop();
                    // } else if (!context.isAllSuspectsSpokenTo()
                    //     && CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You  ABCD did not speak to every suspect during your investigation!\n"
                    //           + "Without doing this, the investigation is incomplete!\n"
                    //           + "Click play again to replay.");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // } else if (context.isAllSuspectsSpokenTo()
                    //     && !CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You did not find any clues in the crime scene!\n"
                    //           + "Finding clues is vital to conduting a good investigation!\n"
                    //           + "Click play again to replay");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // } else if (!context.isAllSuspectsSpokenTo()
                    //     && !CrimeSceneController.isAnyClueFound()
                    //     && context.getGameState().equals(context.getGameStartedState())) {
                    //   context.setState(context.getGameOverState());
                    //   GameOverController.setOutputText(
                    //       "You did not inspect the crime scene for clues or speak to every"
                    //           + " suspect!\n"
                    //           + "These steps are vital in any investigation.\n"
                    //           + "Click play again to replay.");
                    //   try {
                    //     timeline.stop();
                    //     App.setRoot("gamelost");
                    //     return;
                    //   } catch (IOException e) {
                    //     e.printStackTrace();
                    //   }
                    // }

                    timeline.stop();
                  }
                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeToCount));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    // play an instruction sound when entering the room for the first time
    // Media media = new Media(getClass().getResource("/sounds/enter_room.mp3").toExternalForm());
    // MediaPlayer mediaPlayer = new MediaPlayer(media);
    // mediaPlayer.play();
    // isFirstTimeInit = false;
    // }
  }

  /**
   * Gets the rectangle representing the first person in the room.
   *
   * @return
   */
  public Pane getStatsPane() {
    return statsPane;
  }

  /** disables all the rectangles in the room */
  public void disableAll() {
    officer.setDisable(true);
    officer2.setDisable(true);
    trashBin.setDisable(true);
    camera.setDisable(true);
    dashcam.setDisable(true);
    car.setDisable(true);
  }

  /** boolean variable to check if the user has talked the suspects */
  public void talked() {
    hasTalked = true;
  }

  /** Disables all the rectangles in the room when called */
  public void noTalking() {
    // Rectangle person 1 2 3 are disabled to talk
    rectPerson1.setDisable(true);
    rectPerson2.setDisable(true);
    rectPerson3.setDisable(true);
    // officer and officer 2 are disabled to talk
    officer.setDisable(true);
    officer2.setDisable(true);
    // send button is disabled to send messages
    buttonSend.setDisable(true);
    // images are disabled to be clicked
    workerImage.setDisable(true);
    ownerImage.setDisable(true);
    brotherImage.setDisable(true);
  }

  /** Enables all the rectangles in the room when called */
  public void enableTalking() {
    // Rectangle person 1 2 3 are enabled to talk again
    rectPerson1.setDisable(false);
    rectPerson2.setDisable(false);
    rectPerson3.setDisable(false);
    // officer and officer 2 are enabled to talk again
    officer.setDisable(false);
    officer2.setDisable(false);
    // send button is enabled to send messages
    buttonSend.setDisable(false);
    // images are enabled to be clicked
    workerImage.setDisable(false);
    ownerImage.setDisable(false);
    brotherImage.setDisable(false);
  }

  /**
   * Gets the rectangle representing the dashcam.
   *
   * @return
   */
  public Rectangle getDashcam() {
    return dashcam;
  }

  /**
   * gets the Back button
   *
   * @return
   */
  public Button getBtnBack() {
    return buttonBack;
  }

  /** boolean variable representing the wallet */
  public void foundWallet() {
    walletFound = true;
  }

  /**
   * boolean variable representing the found wallet
   *
   * @return
   */
  public Boolean isWalletFound() {
    return walletFound;
  }

  /** boolean variable representing the found camera */
  public void foundCamera() {
    cameraFound = true;
  }

  /** boolean variable representing the found car */
  public void foundCar() {
    isCarFound = true;
  }

  /**
   * boolean variable representing the found camera found
   *
   * @return
   */
  public Boolean isCameraFound() {
    return cameraFound;
  }

  /**
   * getter method for the hasTalked boolean variable
   *
   * @return
   */
  public Boolean getHasTalked() {
    return hasTalked;
  }

  /**
   * boolean variable representing the found dashcam
   *
   * @return
   */
  public Boolean isDashcamFound() {
    return dashcamFound;
  }

  /**
   * boolean variable representing the found car
   *
   * @return
   */
  public Boolean isCarFound() {
    return isCarFound;
  }

  /**
   * getter method for the guess button
   *
   * @return
   */
  public Button getBtnGuess() {
    return buttonGuess;
  }

  /** method for stopping the timeline */
  public void stopTimeLine() {
    timeline.stop();
  }

  /** getter method for getting car image */
  public ImageView getCarImage() {
    return carImage;
  }

  /**
   * getter method for getting the context
   *
   * @return
   */
  public GameStateContext getContext() {
    return context;
  }

  /**
   * setter method for setting chat stats
   *
   * @param stats
   */
  public void setChatStats(String stats) {
    chatStats.setText(stats);
  }

  /** method for disabling the rectangles in the room */
  public void diableRectangles() {
    // disables the rectangles in the room
    rectPerson1.setDisable(true);
    rectPerson2.setDisable(true);
    rectPerson3.setDisable(true);
    // disables the officer and officer 2
    officer.setDisable(true);
    officer2.setDisable(true);
    // disables the trash bin
    trashBin.setDisable(true);
    // disables the camera and dashcam
    dashcam.setDisable(false);
    // disables the car
    car.setDisable(true);
  }

  /** method for enabling the rectangles in the room */
  public void enableRectangles() {
    // enables the rectangles in the room
    rectPerson1.setDisable(false);
    rectPerson2.setDisable(false);
    rectPerson3.setDisable(false);
    // enables the officer and officer 2
    officer.setDisable(false);
    officer2.setDisable(false);
    // enables the trash bin
    trashBin.setDisable(false);
    // enables the camera and dashcam
    car.setDisable(false);
    // enables the car
    dashcam.setDisable(true);
  }

  /**
   * method for getting the person
   *
   * @return
   */
  public Person getPerson() {
    return person;
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles the event when the crime scene icon is clicked. This method is triggered by a mouse
   * click event on the crime scene element. It performs the necessary actions to transition to the
   * crime scene view.
   *
   * @param event the MouseEvent that triggered this handler
   * @throws IOException if an input or output exception occurs
   * @throws ApiProxyException if there is an issue with the API proxy
   */
  @FXML
  void onCrimeSceneClicked(MouseEvent event) throws ApiProxyException, IOException {
    Scene sceneOfButton = buttonGuess.getScene();
    imagesVerticalBox.setVisible(false);
    sceneOfButton.setRoot(SceneManager.getRoot(SceneManager.Scene.CRIME));
    passTimeToCrimeScene(timeToCount);
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   * @throws URISyntaxException
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException, URISyntaxException {
    // Before switching to guess scene, check the user has spoken to all 3 suspects and seen at
    // least one clue
    if (context.isAllSuspectsSpokenTo() && CrimeSceneController.isAnyClueFound()) {
      timeline.stop();
      context.setState(context.getGuessingState());
      Utils.setTimeUsed(timeToCount);
      App.setRoot("guess");
    } else if (!context.isAllSuspectsSpokenTo() && CrimeSceneController.isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/missing_suspect.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    } else if (context.isAllSuspectsSpokenTo() && !CrimeSceneController.isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/clue_reminder_1.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    } else if (!context.isAllSuspectsSpokenTo() && !CrimeSceneController.isAnyClueFound()) {
      Media sound =
          new Media(App.class.getResource("/sounds/keep_investigating.mp3").toURI().toString());
      player = new MediaPlayer(sound);
      player.play();
      return;
    }
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    // map.put("profession", person.getProfession());
    // map.put("name", person.getName());
    map.put("role", person.getRole());
    // if (person.hasTalked()) {
    //   return PromptEngineering.getPrompt("chat3.txt", map, person);
    // }
    // return PromptEngineering.getPrompt("chat2.txt", map, person);

    // retrieves the prompt based on the profession of the person
    if (person.getProfession().equals("owner of the other restaurant")) {
      return PromptEngineering.getPrompt("chat.txt", map, person);
    } else if (person.getProfession().equals("worker at the restaurant")) {
      return PromptEngineering.getPrompt("chat2.txt", map, person);
    } else if (person.getProfession().equals("Elder brother of the family")) {
      return PromptEngineering.getPrompt("chat3.txt", map, person);
    } else {
      return "That name doesn't exist";
    }
  }

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setPerson(Person person) {
    if (this.person == person) {
      return;
    }
    // clear the chat text area
    textaChat.clear();
    this.person = person;

    Platform.runLater(
        () -> {
          // start the progress indicator
          ProgressIndicator statsIndicator = new ProgressIndicator();
          statsIndicator.setMinSize(1, 1);
          statsPane.getChildren().add(statsIndicator);
          // set the chat stats
          context
              .getRoomController()
              .setChatStats("Talking to " + context.getRoomController().getPerson().getName());
        });
    // initialize the chat completion request
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      // run the GPT model
      runGpt(new ChatMessage("system", getSystemPrompt()));
      person.talked();
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    textaChat.appendText(Utils.getPlayerName() + ": " + msg.getContent() + "\n\n");
  }

  private void appendChatMessage(ChatMessage msg, Person person) {
    textaChat.appendText(person.getName() + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // get the specific persons chat completion request
    chatCompletionRequest = person.getChatCompletionRequest();
    // add the message to the chat completion request
    chatCompletionRequest.addMessage(msg);
    try {
      // execute the chat completion request
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      // append the chat message to the chat text area
      appendChatMessage(result.getChatMessage(), person);
      Platform.runLater(
          () -> {
            context.getRoomController().enableTalking();
            context.getRoomController().getStatsPane().getChildren().clear();
          });
      // speak the chat message
      TextToSpeech.speak(result.getChatMessage().getContent(), context);
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    // get the message from the text field
    String message = textInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    // clear the text field
    textInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);

    // start the progress indicator
    ProgressIndicator statsIndicator = new ProgressIndicator();
    statsIndicator.setMinSize(1, 1);
    statsPane.getChildren().add(statsIndicator);

    noTalking();
    // start task to avoid blocking the UI thread
    Task<Void> task =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // run the GPT model
            runGpt(msg);
            return null;
          }
        };

    Thread backgroundThread = new Thread(task);
    backgroundThread.start();
  }

  @FXML
  public void onBackPressed() {
    enableRectangles();
    carImage.setVisible(false);
    buttonBack.setVisible(false);
    buttonBack.setDisable(true);
  }

  /**
   * Handles the styles of the scene for the room view and sets the images of the scene switches.
   */
  @FXML
  public void styleScene() {
    // if the crime scene loader is not null, get the crime scene controller
    if (SceneManager.getCrimeSceneLoader() != null) {
      CrimeSceneController crimeSceneController =
          SceneManager.getCrimeSceneLoader().getController();
      switch (crimeSceneController.getId()) {
        // if the id is owner image, set the person to the owner
        case "ownerImage":
          displayImage.setImage(new Image(ownerImage.getImage().getUrl()));
          setPerson(context.getPerson("rectPerson2"));
        // if the id is worker image, set the person to the worker
        case "workerImage":
          displayImage.setImage(new Image(workerImage.getImage().getUrl()));
          setPerson(context.getPerson("rectPerson1"));
        // if the id is brother image, set the person to the brother
        case "brotherImage":
          displayImage.setImage(new Image(brotherImage.getImage().getUrl()));
          setPerson(context.getPerson("rectPerson3"));
      }
    }
    // set the owners images to be hoverable
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
    // set the workers images to be hoverable
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
    // set the brothers images to be hoverable
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
    // set the crime images to be hoverable
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

    buttonSlide.setOnAction(event -> toggleHorizontalBox());
  }

  /** Enables the images of the scene switches. */
  @FXML
  public void enableImages() {
    ownerImage.setDisable(false);
    workerImage.setDisable(false);
    brotherImage.setDisable(false);
    crimeImage.setDisable(false);
  }

  /**
   * Handles the event when an image is clicked. Depending on the clicked image, it either displays
   *
   * @param event
   * @throws IOException
   * @throws InterruptedException
   */
  @FXML
  public void handleImageClick(MouseEvent event) throws IOException, InterruptedException {
    ImageView clickedImage = (ImageView) event.getSource();
    String id = clickedImage.getId();

    TranslateTransition transition =
        new TranslateTransition(Duration.seconds(0.5), imagesVerticalBox);
    switch (id) {
      case "ownerImage":
        if (currentImage != null && currentImage.getId().equals("ownerImage")) {
          return;
        }
        displayImage.setImage(new Image(ownerImage.getImage().getUrl()));
        currentImage = ownerImage;
        currentImageManager.setImageView(currentImage);
        context.person1Talked();
        transition.setToX(imagesVerticalBox.getWidth() + 30); // Move off-screen
        transition.setOnFinished(e -> imagesVerticalBox.setVisible(false)); // Hide after animation
        transition.play();
        context.handleRectangleClick(event, "rectPerson2");
        break;
      case "workerImage":
        if (currentImage != null && currentImage.getId().equals("workerImage")) {
          return;
        }
        displayImage.setImage(new Image(workerImage.getImage().getUrl()));
        currentImage = workerImage;
        currentImageManager.setImageView(currentImage);
        context.person2Talked();
        transition.setToX(imagesVerticalBox.getWidth() + 30); // Move off-screen
        transition.setOnFinished(e -> imagesVerticalBox.setVisible(false)); // Hide after animation
        transition.play();
        context.handleRectangleClick(event, "rectPerson1");
        break;
      case "brotherImage":
        if (currentImage != null && currentImage.getId().equals("brotherImage")) {
          return;
        }
        displayImage.setImage(new Image(brotherImage.getImage().getUrl()));
        currentImage = brotherImage;
        currentImageManager.setImageView(currentImage);
        context.person3Talked();
        transition.setToX(imagesVerticalBox.getWidth() + 30); // Move off-screen
        transition.setOnFinished(e -> imagesVerticalBox.setVisible(false)); // Hide after animation
        transition.play();
        context.handleRectangleClick(event, "rectPerson3");
    }
  }

  /** Toggles the visibility of the images VBox. */
  private void toggleHorizontalBox() {
    // Create the transition
    TranslateTransition transition =
        new TranslateTransition(Duration.seconds(0.5), imagesVerticalBox);

    if (imagesVerticalBox.isVisible()) {
      // Slide out
      transition.setToX(imagesVerticalBox.getWidth() + 30);
      transition.setOnFinished(event -> imagesVerticalBox.setVisible(false));
    } else {
      // Slide in
      imagesVerticalBox.setVisible(true);
      transition.setFromX(imagesVerticalBox.getWidth() + 30);
      transition.setToX(0);
    }

    // Play the transition
    transition.play();
  }

  /**
   * Sets the image of the person clicked.
   *
   * @param event
   * @param id
   * @throws IOException
   */
  public void setPersonImage(MouseEvent event, String id) throws IOException {
    switch (id) {
      // if the id is owner image, set the person to the owner
      case "ownerImage":
        if (currentImage != null && currentImage.getId().equals("ownerImage")) {
          return;
        }
        // set the image of the owner
        displayImage.setImage(new Image(ownerImage.getImage().getUrl()));
        currentImage = ownerImage;
        // set the image of the owner
        currentImageManager.setImageView(currentImage);
        context.person1Talked();
        context.handleRectangleClick(event, "rectPerson2");
        break;
      // if the id is worker image, set the person to the worker
      case "workerImage":
        if (currentImage != null && currentImage.getId().equals("workerImage")) {
          return;
        }
        // set the image of the worker
        displayImage.setImage(new Image(workerImage.getImage().getUrl()));
        currentImage = workerImage;
        // set the image of the worker
        currentImageManager.setImageView(currentImage);
        context.person2Talked();
        context.handleRectangleClick(event, "rectPerson1");
        break;
      // if the id is brother image, set the person to the brother
      case "brotherImage":
        if (currentImage != null && currentImage.getId().equals("brotherImage")) {
          return;
        }
        // set the image of the brother
        displayImage.setImage(new Image(brotherImage.getImage().getUrl()));
        currentImage = brotherImage;
        // set the image of the brother
        currentImageManager.setImageView(currentImage);
        context.person3Talked();
        context.handleRectangleClick(event, "rectPerson3");
        break;
    }
  }

  /**
   * Sets the context for the room controller.
   *
   * @param context
   */
  public void setContext(GameStateContext context) {
    this.context = context;
  }
}
