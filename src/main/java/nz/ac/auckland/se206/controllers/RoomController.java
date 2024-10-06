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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;
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
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();

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
  @FXML private Button buttonSlide;
  @FXML private Button sendButton;

  @FXML private TextArea textaChat;

  @FXML private TextField textInput;
  @FXML private TextField inputField;

  @FXML private ImageView carImage;
  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;
  @FXML private ImageView brotherImage;
  @FXML private ImageView crimeImage;
  @FXML private ImageView displayImage;

  @FXML private StackPane indicatorPane;
  @FXML private Pane statsPane;

  @FXML private VBox imagesVerticalBox;
  @FXML private VBox messageBoxes;

  @FXML private ScrollPane scrollPane;

  private ChatCompletionRequest chatCompletionRequest;
  private Person person;
  private ImageView currentImage = null;

  private ImageManager currentImageManager;
  private ImageManager ownerImageManager;
  private ImageManager workerImageManager;
  private ImageManager brotherImageManager;
  private ImageManager crimeImageManager;

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

    messageBoxes.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));

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
    // send button is disabled to send messages
    buttonSend.setDisable(true);
    // images are disabled to be clicked
    workerImage.setDisable(true);
    ownerImage.setDisable(true);
    brotherImage.setDisable(true);
  }

  /** Enables all the rectangles in the room when called */
  public void enableTalking() {
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
  private void onHandleGuessClick(ActionEvent event) throws IOException, URISyntaxException {
    // Before switching to guess scene, check the user has spoken to all 3 suspects and seen at
    // least one clue
    if (context.isAllSuspectsSpokenTo() && CrimeSceneController.isAnyClueFound()) {
      timeline.stop();
      context.setState(context.getGuessingState());
      Utils.setTimeUsed(TimelineManager.getTimeToCount());
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
    map.put("role", person.getRole());

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
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    Platform.runLater(
        () -> {
          StackPane messageContainer = new StackPane();
          messageContainer.setPadding(new Insets(10));
          HBox hbox = new HBox(messageContainer);
          messageContainer.setStyle(
              "-fx-background-color: white; -fx-background-radius: 15; -fx-effect:"
                  + " dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);");
          messageContainer.setAlignment(Pos.CENTER_LEFT);

          ProgressIndicator statsIndicator2 = new ProgressIndicator();
          statsIndicator2.setPrefSize(18, 18);

          messageContainer.getChildren().add(statsIndicator2);
          messageBoxes.getChildren().add(hbox);
        });
    // get the specific persons chat completion request
    chatCompletionRequest = person.getChatCompletionRequest();
    // add the message to the chat completion request
    chatCompletionRequest.addMessage(msg);
    try {
      // execute the chat completion request
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      Platform.runLater(
          () -> {
            appendMessage(result.getChatMessage(), false);
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
    String message = inputField.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    // clear the text field
    inputField.clear();
    messageBoxes.getChildren().clear();

    ChatMessage msg = new ChatMessage("user", message);
    appendMessage(msg, true);
    // Platform.runLater(() -> scrollPane.setVvalue(1.0));

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

    // task.setOnSucceeded(
    //     e -> {
    //       Platform.runLater(() -> scrollPane.setVvalue(1.0));
    //     });
    Thread backgroundThread = new Thread(task);
    backgroundThread.start();
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
          break;
        // if the id is worker image, set the person to the worker
        case "workerImage":
          displayImage.setImage(new Image(workerImage.getImage().getUrl()));
          setPerson(context.getPerson("rectPerson1"));
          break;
        // if the id is brother image, set the person to the brother
        case "brotherImage":
          displayImage.setImage(new Image(brotherImage.getImage().getUrl()));
          setPerson(context.getPerson("rectPerson3"));
          break;
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
  @SuppressWarnings("static-access")
  public void setContext(GameStateContext context) {
    this.context = context;
  }

  private void appendMessage(ChatMessage message, boolean isUser) {
    Text text = new Text(message.getContent());
    if (text.getLayoutBounds().getWidth() > 400) {
      text.setWrappingWidth(400);
    }
    // Set background and alignment based on the sender
    if (isUser) {
      StackPane messageContainer = new StackPane();
      messageContainer.setPadding(new Insets(10));
      HBox hbox = new HBox(messageContainer);
      messageContainer.setAlignment(Pos.CENTER_LEFT);
      text.setStyle(
          "-fx-padding: 10; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-text-fill: white;");
      messageContainer.setStyle(
          "-fx-background-color: #eeac5a; -fx-background-radius: 15; -fx-effect:"
              + " dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);");
      messageContainer.setMaxWidth(400);
      hbox.setAlignment(Pos.CENTER_RIGHT);
      messageContainer.getChildren().add(text);
      // Add the message to the message box
      messageBoxes.getChildren().add(hbox);
    } else {
      text.setStyle(
          "-fx-padding: 10; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-text-fill: black;");
      HBox hbox = (HBox) messageBoxes.getChildren().get(messageBoxes.getChildren().size() - 1);
      StackPane messageContainer = (StackPane) hbox.getChildren().get(0);
      messageContainer.getChildren().clear();
      messageContainer.getChildren().add(text);
    }
  }
}
