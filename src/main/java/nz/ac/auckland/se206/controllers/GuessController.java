package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.Person;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;

public class GuessController {
  private static boolean isFirstTimeInit = true;
  private static boolean isTimeOver = false;
  private static boolean hasTalked = false;
  private static boolean walletFound = false;
  private static boolean cameraFound = false;
  private static boolean dashcamFound = false;
  private static boolean isCarFound = false;
  private static GameStateContext context = new GameStateContext();
  private static double timeToCount = 300000;
  private static double timeToCountTo = 360000;
  private static double maxTimeforGuessing = 60000;
  private static double timeForGuessing = 60000;
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
  @FXML private Button sus1btn;
  @FXML private Button sus2btn;
  @FXML private Button sus3btn;

  @FXML private TextArea txtaChat;

  @FXML private TextField txtInput;

  @FXML private ImageView carImage;

  @FXML private StackPane indicatorPane;
  @FXML private Pane statsPane;
  @FXML private Label lblDescription;

  private ChatCompletionRequest chatCompletionRequest;
  private Person person;

  private Timeline timeline = new Timeline();
  private int currentSuspect = 0;
  private boolean selectedSuspect = false;
  private static boolean isThiefFound = false;
  private static GuessController guessController;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {

    // Adding the event handler for 'Enter' key on txtInput
    txtInput.setOnKeyPressed(
        new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
              try {
                // Calling the send message function
                onSendMessage(new ActionEvent());
              } catch (ApiProxyException | IOException e) {
                e.printStackTrace();
              }
            }
          }
        });

    context.setGuessController(this);
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(60);
    // Timer label is updated here
    if (timeToCount % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeForGuessing));
    }

    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeForGuessing > 0) {
                    timeForGuessing--;
                    progress =
                        (int)
                            (100
                                - ((maxTimeforGuessing - timeForGuessing)
                                    * 100
                                    / (maxTimeforGuessing)));
                  } else if ((timeForGuessing == 0)) {
                    System.out.println("Switching to game over scene and and state");
                    context.setState(context.getGameOverState());
                    try {
                      App.setRoot("gameover");
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    // Here we need to implement functionality that happens when the guessing time
                    // runs out.
                    // Either the user has guessed and explained, or they haven't(immediately report
                    // a loss), so we should account for each case.
                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeForGuessing));
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    // Media media = new Media(getClass().getResource("/sounds/enter_room.mp3").toExternalForm());
    // MediaPlayer mediaPlayer = new MediaPlayer(media);
    // mediaPlayer.play();

  }

  public Pane getStatsPane() {
    return statsPane;
  }

  public Boolean getTimeOver() {
    return isTimeOver;
  }

  public Boolean getSuspectSelected() {
    return selectedSuspect;
  }

  public void stopTimeLine() {
    timeline.stop();
  }

  public GameStateContext getContext() {
    return context;
  }

  public void setChatStats(String stats) {
    chatStats.setText(stats);
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
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", person.getProfession());
    map.put("name", person.getName());
    map.put("role", person.getRole());

    // Map<String, String> map = new HashMap<>();
    // map.put("profession", profession);
    // return PromptEngineering.getPrompt("chat.txt", map);

    if (person.hasTalked()) {
      return PromptEngineering.getPrompt("chat3.txt", map, person);
    }
    return PromptEngineering.getPrompt("chat2.txt", map, person);
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    txtInput.appendText(msg.getContent() + "\n\n");
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
      // TextToSpeech.speak(result.getChatMessage().getContent(), context);
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  @FXML
  private void selectSuspect1(ActionEvent event) throws ApiProxyException, IOException {
    sus1btn.setDisable(true);
    sus2btn.setDisable(false);
    sus3btn.setDisable(false);
    currentSuspect = 1;
    isThiefFound = false;
    selectedSuspect = true;
  }

  @FXML
  private void selectSuspect2(ActionEvent event) throws ApiProxyException, IOException {
    sus1btn.setDisable(false);
    sus2btn.setDisable(true);
    sus3btn.setDisable(false);
    currentSuspect = 2;
    isThiefFound = true;
    selectedSuspect = true;
  }

  @FXML
  private void selectSuspect3(ActionEvent event) throws ApiProxyException, IOException {
    sus1btn.setDisable(false);
    sus2btn.setDisable(false);
    sus3btn.setDisable(true);
    currentSuspect = 3;
    isThiefFound = false;
    selectedSuspect = true;
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
      lblDescription.setText("Empty message");
      System.out.println("Empty message");
      return;
    }

    if (selectedSuspect == false) {
      lblDescription.setText("No suspect selected");
      System.out.println("No suspect selected");
      return;
    }

    // gameOverController.setGuessController(this);
    App.setRoot("gameover");

    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);

    setChatStats(" loading...");

    ProgressIndicator statsIndicator = new ProgressIndicator();
    statsIndicator.setMinSize(1, 1);
    statsPane.getChildren().add(statsIndicator);

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

    // App.setRoot("gameover");

  }

  public int getSuspectNumber() {
    return currentSuspect;
  }

  public static boolean getThiefFound() {
    return isThiefFound;
  }

  public GuessController getGuessController() {
    return this.guessController;
  }

  public static void setTimeToGuess(double time) {
    // Adds any leftover time from the investigation scene to the time available for guessing.
    timeForGuessing = timeForGuessing + time;
    maxTimeforGuessing = maxTimeforGuessing + time;
  }
}
