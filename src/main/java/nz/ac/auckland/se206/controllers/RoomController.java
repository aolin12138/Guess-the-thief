package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.Person;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {
  private static boolean isFirstTimeInit = true;
  private static boolean isTimeOver = false;
  private static boolean hasTalked = false;
  private static boolean walletFound = false;
  private static boolean cameraFound = false;
  private static boolean dashcamFound = false;
  private static boolean isCarFound = false;
  private static GameStateContext context = new GameStateContext();
  private static int timeToCount = 120;
  private static int timeToCountTo = 120;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();

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

  @FXML private Button btnGuess;
  @FXML private Button btnSend;
  @FXML private Button btnBack;

  @FXML private TextArea txtaChat;

  @FXML private TextField txtInput;

  @FXML private ImageView carImage;
  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;

  @FXML private StackPane indicatorPane;
  @FXML private Pane statsPane;

  private ChatCompletionRequest chatCompletionRequest;
  private Person person;

  private Timeline timeline = new Timeline();

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      styleScene();
      context.setRoomController(this);
      indicatorPane.getChildren().add(ringProgressIndicator);
      ringProgressIndicator.setRingWidth(50);
      timerLabel.setText(String.format("%02d", timeToCount));

      timeline
          .getKeyFrames()
          .add(
              new KeyFrame(
                  Duration.seconds(1),
                  event -> {
                    if (timeToCount > 0) {
                      timeToCount--;
                      progress = (int) ((timeToCountTo - timeToCount) * 100 / timeToCountTo);
                    } else if (isTimeOver == false) {
                      Platform.runLater(
                          () -> {
                            timeline.stop();
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Time's up!");
                            alert.setHeaderText("Time's up! You need to choose now!");
                            alert.setContentText("Click on the thief.");
                            alert.showAndWait();
                            disableAll();
                            timeline.play();
                          });
                      btnGuess.setDisable(true);
                      btnSend.setDisable(true);
                      context.setState(context.getGuessingState());
                      isTimeOver = true;
                      timeToCountTo = 10;
                      timeToCount = 10;
                      progress = 0;
                    } else {
                      timeline.stop();
                      Platform.runLater(
                          () -> {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Game Over");
                            alert.setHeaderText("Game Over");
                            alert.setContentText("You Lost! You did not find the thief in time.");
                            alert.showAndWait();
                          });
                      context.setState(context.getGameOverState());
                    }

                    ringProgressIndicator.setProgress(progress);
                    timerLabel.setText(String.format("%02d", timeToCount));
                  }));
      timeline.setCycleCount(Timeline.INDEFINITE);
      timeline.play();
      Media media = new Media(getClass().getResource("/sounds/enter_room.mp3").toExternalForm());
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      mediaPlayer.play();
      isFirstTimeInit = false;
    }
  }

  public Pane getStatsPane() {
    return statsPane;
  }

  public Boolean getTimeOver() {
    return isTimeOver;
  }

  public void disableAll() {
    officer.setDisable(true);
    officer2.setDisable(true);
    trashBin.setDisable(true);
    camera.setDisable(true);
    dashcam.setDisable(true);
    car.setDisable(true);
  }

  public void talked() {
    hasTalked = true;
  }

  public void noTalking() {
    rectPerson1.setDisable(true);
    rectPerson2.setDisable(true);
    rectPerson3.setDisable(true);
    officer.setDisable(true);
    officer2.setDisable(true);
    btnSend.setDisable(true);
  }

  public void enableTalking() {
    rectPerson1.setDisable(false);
    rectPerson2.setDisable(false);
    rectPerson3.setDisable(false);
    officer.setDisable(false);
    officer2.setDisable(false);
    btnSend.setDisable(false);
  }

  public Rectangle getDashcam() {
    return dashcam;
  }

  public Button getBtnBack() {
    return btnBack;
  }

  public void foundWallet() {
    walletFound = true;
  }

  public Boolean isWalletFound() {
    return walletFound;
  }

  public void foundCamera() {
    cameraFound = true;
  }

  public void foundCar() {
    isCarFound = true;
  }

  public Boolean isCameraFound() {
    return cameraFound;
  }

  public Boolean getHasTalked() {
    return hasTalked;
  }

  public Boolean isDashcamFound() {
    return dashcamFound;
  }

  public Boolean isCarFound() {
    return isCarFound;
  }

  public Button getBtnGuess() {
    return btnGuess;
  }

  public void stopTimeLine() {
    timeline.stop();
  }

  public ImageView getCarImage() {
    return carImage;
  }

  public GameStateContext getContext() {
    return context;
  }

  public void setChatStats(String stats) {
    chatStats.setText(stats);
  }

  public void diableRectangles() {
    rectPerson1.setDisable(true);
    rectPerson2.setDisable(true);
    rectPerson3.setDisable(true);
    officer.setDisable(true);
    officer2.setDisable(true);
    trashBin.setDisable(true);
    dashcam.setDisable(false);
    car.setDisable(true);
  }

  public void enableRectangles() {
    rectPerson1.setDisable(false);
    rectPerson2.setDisable(false);
    rectPerson3.setDisable(false);
    officer.setDisable(false);
    officer2.setDisable(false);
    trashBin.setDisable(false);
    car.setDisable(false);
    dashcam.setDisable(true);
  }

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
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    context.handleGuessClick();
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", person.getProfession());
    map.put("name", person.getName());
    map.put("role", person.getRole());
    if (person.hasTalked()) {
      return PromptEngineering.getPrompt("chat3.txt", map, person);
    }
    return PromptEngineering.getPrompt("chat2.txt", map, person);
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

    txtaChat.clear();
    this.person = person;
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
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
    txtaChat.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());
      Platform.runLater(
          () -> {
            context.getRoomController().enableTalking();
            context
                .getRoomController()
                .setChatStats(
                    "Talking to "
                        + context.getRoomController().getPerson().getName()
                        + " who is in "
                        + context.getRoomController().getPerson().getColor());
            context.getRoomController().getStatsPane().getChildren().clear();
          });
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
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    if (context.getGameState() == context.getGameOverState()) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Game Over");
      alert.setHeaderText("Game Over");
      alert.setContentText("You can not talk to the suspects anymore.");
      alert.showAndWait();
      txtInput.clear();
      return;
    }

    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);

    setChatStats(person.getName() + " is gathering words...");

    ProgressIndicator statsIndicator = new ProgressIndicator();
    statsIndicator.setMinSize(1, 1);
    statsPane.getChildren().add(statsIndicator);

    noTalking();
    Task<Void> task =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            runGpt(msg);
            return null;
          }
        };

    // task.setOnSucceeded(
    //     event1 -> {
    //       statsPane.getChildren().remove(statsIndicator);
    //       // setChatStats("Talking to " + person.getName() + " who is in " + person.getColor());
    //       enableTalking();
    //     });
    Thread backgroundThread = new Thread(task);
    backgroundThread.start();
  }

  @FXML
  public void handleDashcamClick(MouseEvent event) throws IOException {
    dashcamFound = true;
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Dashcam footage found");
    alert.setHeaderText(
        "A person in "
            + context.getPersonToGuess().getColor()
            + " clothes was seen near the crime scene.");
    alert.showAndWait();
    enableRectangles();
    carImage.setVisible(false);
    btnBack.setVisible(false);
    btnBack.setDisable(true);
  }

  @FXML
  public void onBackPressed() {
    enableRectangles();
    carImage.setVisible(false);
    btnBack.setVisible(false);
    btnBack.setDisable(true);
  }

  @FXML
  public void styleScene() {
    ScaleTransition scaleTransitionIn = new ScaleTransition(Duration.millis(200), ownerImage);
    scaleTransitionIn.setFromX(1.0);
    scaleTransitionIn.setFromY(1.0);
    scaleTransitionIn.setToX(1.1);
    scaleTransitionIn.setToY(1.1);
    scaleTransitionIn.setCycleCount(1);

    ScaleTransition scaleTransitionOut = new ScaleTransition(Duration.millis(200), ownerImage);
    scaleTransitionOut.setFromX(1.1);
    scaleTransitionOut.setFromY(1.1);
    scaleTransitionOut.setToX(1.0);
    scaleTransitionOut.setToY(1.0);
    scaleTransitionOut.setCycleCount(1);

    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.45);

    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(0);
    dropShadow.setOffsetX(0);
    dropShadow.setOffsetY(0);
    dropShadow.setColor(javafx.scene.paint.Color.GRAY);
    dropShadow.setInput(colorAdjust);
    ownerImage.setEffect(dropShadow);
    workerImage.setEffect(dropShadow);

    Timeline brightnessTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), -0.45)),
            new KeyFrame(Duration.millis(200), new KeyValue(colorAdjust.brightnessProperty(), 0)));
    timeline.setCycleCount(1);

    Timeline brightnessTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.brightnessProperty(), 0)),
            new KeyFrame(
                Duration.millis(200), new KeyValue(colorAdjust.brightnessProperty(), -0.45)));
    timeline.setCycleCount(1);

    Timeline shadowTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadow.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(200), new KeyValue(dropShadow.radiusProperty(), 10)));
    timeline.setCycleCount(1);

    Timeline shadowTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadow.radiusProperty(), 10)),
            new KeyFrame(Duration.millis(200), new KeyValue(dropShadow.radiusProperty(), 0)));

    ownerImage.setOnMouseEntered(
        e -> {
          scaleTransitionIn.play();
          brightnessTransitionIn.play();
          shadowTransitionIn.play();
        });
    ownerImage.setOnMouseExited(
        e -> {
          scaleTransitionOut.play();
          brightnessTransitionOut.play();
          shadowTransitionOut.play();
        });
  }
}
