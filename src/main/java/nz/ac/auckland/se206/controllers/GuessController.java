package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.Person;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.ringIndicator.RingProgressIndicator;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class GuessController {
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
     txtInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
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

    if (isFirstTimeInit) {
      context.setGuessController(this);
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
                            // disableAll();
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
    // map.put("profession", person.getProfession());
    map.put("name", person.getName());
    // map.put("role", person.getRole());

    // if (person.hasTalked()) {
    //   return PromptEngineering.getPrompt("chat3.txt", map, person);
    // }
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
        // App.setRoot("gameover");
    
    if(selectedSuspect) {
    
      Task<Void> task = new Task<Void>(){
        @Override 
        protected Void call() throws Exception{
          try{
            String validExplanation = isExplanationValid();

            GameOverController.setOutputText(validExplanation);

            String[] split = validExplanation.trim().split("");
            boolean valid = 
            // split[0].toLowerCase().contains("yes");
            currentSuspect == 2;

            Platform.runLater(
              () -> {
                // stopLoadingAnimation();

                // GuessTimeLimitManager.stopTimer();

                if(valid) {
                  context.setState(context.getGameOverState());

                  try {

                    App.setRoot("gameover");
                  }catch(IOException e) {
                    e.printStackTrace();
                  }
                } else {
                  context.setState(context.getGameOverState());

                  try{
                    // GameOverController.setCorrectSuspect(true);

                    App.setRoot("gamelost");
                  }catch(IOException e) {
                    e.printStackTrace();
                  }
                }
              });
            }catch(ApiProxyException e) {
              e.printStackTrace();
                }
                return null;
              }

            };

            sus1btn.setDisable(true);
            sus2btn.setDisable(true);
            sus3btn.setDisable(true);
            txtInput.setDisable(true);

            new Thread(task).start();
          } else {

            // GameOverController.setCorrectSuspect(false);

            // GuessTimeLimitManager.stopTimer();

            context.setState(context.getGameOverState());
            App.setRoot("gamelost");
          }
        }

              
    
        public String isExplanationValid() throws ApiProxyException, IOException {
            try {
              String evidencePrompt =
              new String(Files.readAllBytes(Paths.get("src/main/resources/prompts/chat2.txt")));

      String fullPrompt =
          evidencePrompt + "\nUser Reasoning:\n" + txtInput.getText() + "\n";

      ChatCompletionRequest request =
          new ChatCompletionRequest(ApiProxyConfig.readConfig())
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(150)
              .addMessage(
                  new ChatMessage(
                      "system",
                      "You are an expert at evaluating reasoning based on clues and evidence."));

      request.addMessage(new ChatMessage("user", fullPrompt));

      ChatCompletionResult result = request.execute();
      String response = result.getChoices().iterator().next().getChatMessage().getContent().trim();
      return response;
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }

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



}
