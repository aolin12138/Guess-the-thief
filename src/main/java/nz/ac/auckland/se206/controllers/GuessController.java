package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.ImageManager;
import nz.ac.auckland.se206.Person;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.TimelineManager;
import nz.ac.auckland.se206.Utils;
import nz.ac.auckland.se206.ringindicator.RingProgressIndicator;

public class GuessController {
  private static boolean isTimeOver = false;
  private static GameStateContext context = new GameStateContext();
  private static double maxTimeforGuessing = 60000;
  private static double timeForGuessing = 60000;
  private static int progress = 0;
  private static RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
  private static boolean isThiefFound = false;
  private static boolean isGameWon = false;

  public static boolean getThiefFound() {
    return isThiefFound;
  }

  public static boolean getIsGameWon() {
    return isGameWon;
  }

  public static void setGameStateContext(GameStateContext gameStateContext) {
    context = gameStateContext;
  }

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

  @FXML private Button buttonGuess;
  @FXML private Button buttonSend;
  @FXML private Button buttonBack;
  @FXML private Button sus1btn;
  @FXML private Button sus2btn;
  @FXML private Button sus3btn;
  @FXML private Button restartButton;

  @FXML private TextArea textaChat;

  @FXML private TextField textInput;

  @FXML private ImageView carImage;
  @FXML private ImageView ownerImage;
  @FXML private ImageView workerImage;
  @FXML private ImageView brotherImage;
  @FXML private ImageView crimeScene;

  @FXML private StackPane indicatorPane;
  @FXML private StackPane loadingPane;
  @FXML private Pane statsPane;
  @FXML private Label lblDescription;
  @FXML private Label ownerLabel;
  @FXML private Label workerLabel;
  @FXML private Label brotherLabel;
  @FXML private Label leaderboardResultLabel;
  @FXML private Label explanationLabel;
  @FXML private Label instructionLabel;

  @FXML private TextArea guessTextArea;

  @FXML private HBox chatHorizontalBox;

  private Person person;

  private Timeline timeline = new Timeline();
  private int currentSuspect = 0;
  private boolean isSuspectSelected = false;

  private Label currentLabel;

  private ImageManager ownerImageManager;
  private ImageManager workerImageManager;
  private ImageManager brotherImageManager;
  private ImageManager currentImageManager;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    // set the state to the guessing state
    context.setState(context.getGuessingState());
    textInput.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");

    buttonSend
        .sceneProperty()
        .addListener(
            (observable, oldScene, newScene) -> {
              if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.sizeToScene();
              }
            });

    brotherImage.setCursor(Cursor.HAND);
    workerImage.setCursor(Cursor.HAND);
    ownerImage.setCursor(Cursor.HAND);
    buttonSend.setCursor(Cursor.HAND);
    restartButton.setCursor(Cursor.HAND);
    restartButton.setVisible(false);
    guessTextArea.setVisible(false);
    leaderboardResultLabel.setVisible(false);

    // Adding the event handler for 'Enter' key on txtInput
    textInput.setOnKeyPressed(
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
    // Set the image managers for the suspects
    ownerImageManager = new ImageManager(ownerImage);
    workerImageManager = new ImageManager(workerImage);
    brotherImageManager = new ImageManager(brotherImage);
    // adjust the brightness of the images
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(-0.45);
    ownerImage.setEffect(colorAdjust);
    workerImage.setEffect(colorAdjust);
    brotherImage.setEffect(colorAdjust);

    styleScene();
    // Set the guess controller to this instance
    context.setGuessController(this);
    indicatorPane.getChildren().add(ringProgressIndicator);
    ringProgressIndicator.setRingWidth(60);
    // Timer label is updated here
    if (timeForGuessing % 1000 == 0) {
      timerLabel.setText(Utils.formatTime(timeForGuessing));
    }
    // use the time line to update the progress indicator and timer label
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeForGuessing > 0) {
                    // This runs when there is still time on the clock
                    timeForGuessing--;
                    progress =
                        (int)
                            (100
                                - ((maxTimeforGuessing - timeForGuessing)
                                    * 100
                                    / (maxTimeforGuessing)));
                  } else if ((timeForGuessing == 0)) {
                    isTimeOver = true;
                    // Call the onSendMessage without clicking button (input therefore null, but
                    // isn't required anyway.)
                    // Before switching state, make sure the game is still in the game started state
                    // and that we havent already switched state. Otherwise it will cause a bug
                    if (!(context.getGameState().equals(context.getGuessingState()))) {
                      timeline.stop();
                      return;
                    }
                    try {
                      onSendMessage(null);
                    } catch (ApiProxyException | IOException e) {
                      e.printStackTrace();
                    }

                    timeline.stop();
                  }

                  ringProgressIndicator.setProgress(progress);
                  timerLabel.setText(Utils.formatTime(timeForGuessing));
                  // flash the timer red below 15 seconds
                  if (timeForGuessing < 15000) {
                    if ((int) (timeForGuessing / 1000) % 2 == 0) {
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
   * Gets the stats pane.
   *
   * @return the time for guessing
   */
  public Pane getStatsPane() {
    return statsPane;
  }

  /**
   * Gets the boolean for if the time is over.
   *
   * @return the time for guessing
   */
  public Boolean getTimeOver() {
    return isTimeOver;
  }

  /**
   * Gets the boolean for if the suspect is selected.
   *
   * @return the time for guessing
   */
  public Boolean getSuspectSelected() {
    return isSuspectSelected;
  }

  /**
   * method that stops the timeline.
   *
   * @return the time for guessing
   */
  public void stopTimeLine() {
    timeline.stop();
  }

  /**
   * Gets the current context.
   *
   * @return the time for guessing
   */
  public GameStateContext getContext() {
    return context;
  }

  /**
   * set the chat stats.
   *
   * @param stats
   */
  public void setChatStats(String stats) {
    chatStats.setText(stats);
  }

  /**
   * gets the current person.
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
   * selects the first suspect
   *
   * @param event
   * @throws ApiProxyException
   * @throws IOException
   */
  @FXML
  private void selectSuspect1(MouseEvent event) throws ApiProxyException, IOException {
    toggleHorizontalBox();
    // If the current suspect is the owner, return
    if (currentImageManager == ownerImageManager) {
      return;
    }
    // If there is a current label, set it to invisible
    if (currentLabel != null) {
      currentLabel.setVisible(false);
    }
    // If there is a current image manager, set it to unclicked
    if (currentImageManager != null) {
      currentImageManager.unclicked();
    }
    // Set the owner image manager to clicked and visible
    ownerImageManager.clicked();
    ownerLabel.setVisible(true);
    currentLabel = ownerLabel;
    currentImageManager = ownerImageManager;
    currentSuspect = 1;
    isThiefFound = false;
    isSuspectSelected = true;
  }

  /**
   * selects the second suspect
   *
   * @param event
   * @throws ApiProxyException
   * @throws IOException
   */
  @FXML
  private void selectSuspect2(MouseEvent event) throws ApiProxyException, IOException {
    toggleHorizontalBox();
    // If the current suspect is the worker, return
    if (currentImageManager == workerImageManager) {
      return;
    }
    // If there is a current label, set it to invisible
    if (currentLabel != null) {
      currentLabel.setVisible(false);
    }
    //  If there is a current image manager, set it to unclicked
    if (currentImageManager != null) {
      currentImageManager.unclicked();
    }
    // Set the worker image manager to clicked and visible
    workerImageManager.clicked();
    workerLabel.setVisible(true);
    currentLabel = workerLabel;
    currentImageManager = workerImageManager;
    currentSuspect = 2;
    isThiefFound = false;
    isSuspectSelected = true;
  }

  /**
   * selects the third suspect
   *
   * @param event
   * @throws ApiProxyException
   * @throws IOException
   */
  @FXML
  private void selectSuspect3(MouseEvent event) throws ApiProxyException, IOException {
    toggleHorizontalBox();
    // If the current suspect is the brother, return
    if (currentImageManager == brotherImageManager) {
      return;
    }
    // If there is a current label, set it to invisible
    if (currentLabel != null) {
      currentLabel.setVisible(false);
    }
    // If there is a current image manager, set it to unclicked
    if (currentImageManager != null) {
      currentImageManager.unclicked();
    }
    // Set the brother image manager to clicked and visible
    brotherImageManager.clicked();
    brotherLabel.setVisible(true);
    currentLabel = brotherLabel;
    currentImageManager = brotherImageManager;
    currentSuspect = 3;
    isThiefFound = true;
    isSuspectSelected = true;
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

    String message = textInput.getText().trim();

    // No time remaining
    if ((!isSuspectSelected)
        && (message.isEmpty())
        && (isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      context.setState(context.getGameOverState());
      // Set the output text to the explanation of the guess
      guessTextArea.appendText(
          "You did not guess any of the suspects within the time limit!\n"
              + "Next time you play, make sure to click on your suspected thief and"
              + " type an explanation to support your decision.\n"
              + "Click play again to replay.");

      timeline.stop();
      return;
      // No suspect selected, but message is entered and time is over
    } else if ((!isSuspectSelected)
        && (!message.isEmpty())
        && (isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      context.setState(context.getGameOverState());
      // Set the output text to the explanation of the guess
      guessTextArea.appendText(
          "Even though you typed your explanation, you did not guess any of the suspects within the"
              + " time limit!\n"
              + "Click play again to replay.");

      timeline.stop();
      return;
      // Suspect selected, but message is not entered and time is over
    } else if ((isSuspectSelected)
        && (message.isEmpty())
        && (isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      context.setState(context.getGameOverState());
      // Set the output text to the explanation of the guess
      guessTextArea.appendText(
          "Even though you guessed a suspect, you did not type any explanation within the"
              + " time limit.\n\n"
              + " Because of this, it is unlikely that the authortities will accept your"
              + " decision.\n\n"
              + "Click play again to replay.");
      timeline.stop();
      return;
    }

    // Time remaining, checking that suspect is guessed and explanation is entered.
    if ((!isSuspectSelected)
        && (message.isEmpty())
        && (!isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      // Set the description label to the appropriate message
      lblDescription.setText(
          "You must click on your suspected thief and type a brief explanation to support your"
              + " decision.");
      return;
      // Suspect is selected, but no explanation is entered
    } else if ((!isSuspectSelected)
        && (!message.isEmpty())
        && (!isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      // Set the description label to the appropriate message
      lblDescription.setText("You must click on your suspected thief first!");
      return;
      // Suspect is not selected, but explanation is entered
    } else if ((isSuspectSelected)
        && (message.isEmpty())
        && (!isTimeOver)
        && context.getGameState().equals(context.getGuessingState())) {
      // Set the description label to the appropriate message
      lblDescription.setText("You must type an explanation to support your decision.");
      return;
    }
    // Passes the amount of time used to Utils for the scoreboard
    Utils.setTimeUsed(timeForGuessing);
    guessTextArea.appendText(message);
    timeline.stop();
    // Set the progress indicator to visible
    ProgressIndicator statsIndicator = new ProgressIndicator();
    statsIndicator.setMinSize(1, 1);
    statsPane.getChildren().add(statsIndicator);
    // Set the text of the progress indicator to visible
    lblDescription.setText("Evaluating...");
    // Create a new task
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            try {
              // Check if the explanation is valid
              String validExplanation = isExplanationValid();
              String[] splitArray = validExplanation.split(" ", 2);
              // Check if the explanation is correct
              boolean isCorrectExplanation = splitArray[0].toLowerCase().contains("true");
              guessTextArea.appendText(splitArray[1]);
              // Set the chat stats to the explanation
              System.out.println("isCorrectExplanation: " + isCorrectExplanation);

              // Set the chat stats to the explanation
              Platform.runLater(
                  () -> {

                    // GuessTimeLimitManager.stopTimer();
                    System.out.println("is Correct Explanation: " + isCorrectExplanation);
                    System.out.println("currentSuspect: " + currentSuspect);
                    System.out.println("isThiefFound: " + isThiefFound);
                    if (isCorrectExplanation && currentSuspect == 3) {
                      context.setState(context.getGameOverState());
                      isGameWon = true;
                      styleEndOfGame();
                      // need to determine if this score is worthy of being on the leaderboard
                      // if the time is quicker than the 3rd place time of the leaderboard, it is
                      // added.
                      instructionLabel.setText("Congratulations! You are Correct!");
                    } else {
                      context.setState(context.getGameOverState());
                      styleEndOfGame();
                      instructionLabel.setText("Oh no, that's not right!");
                    }
                  });
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };
    // Set the text input to disabled
    textInput.setDisable(true);
    // Start the task in a new thread
    new Thread(task).start();
  }

  /**
   * Checks if the explanation is valid.
   *
   * @return the response
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  public String isExplanationValid() throws ApiProxyException, IOException {

    try {
      // read the evidence prompt from the file
      String evidencePrompt =
          new String(Files.readAllBytes(Paths.get("src/main/resources/prompts/guessing.txt")));

      // generate a full prompt by fetching the user's reasoning
      String fullPrompt = evidencePrompt + "\nUser Reasoning:\n" + textInput.getText() + "\n";
      // Set the chat completion request
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
      // Execute the request
      ChatCompletionResult result = request.execute();
      String response = result.getChoices().iterator().next().getChatMessage().getContent().trim();
      return response;
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This method is called to get the suspect number that the user has selected.
   *
   * @param event
   */
  public int getSuspectNumber() {
    return currentSuspect;
  }

  public void styleScene() {
    // set the owner image to be hovable
    ownerImage.setOnMouseEntered(
        e -> {
          ownerImageManager.hoverIn();
          ownerLabel.setVisible(true);
        });
    ownerImage.setOnMouseExited(
        e -> {
          ownerImageManager.hoverOut();
          if (currentLabel != ownerLabel) {
            ownerLabel.setVisible(false);
          }
        });
    // set the worker image to be hovable
    workerImage.setOnMouseEntered(
        e -> {
          workerImageManager.hoverIn();
          workerLabel.setVisible(true);
        });
    workerImage.setOnMouseExited(
        e -> {
          workerImageManager.hoverOut();
          if (currentLabel != workerLabel) {
            workerLabel.setVisible(false);
          }
        });
    // set the brother image to be hovable
    brotherImage.setOnMouseEntered(
        e -> {
          brotherImageManager.hoverIn();
          brotherLabel.setVisible(true);
        });
    brotherImage.setOnMouseExited(
        e -> {
          brotherImageManager.hoverOut();
          if (currentLabel != brotherLabel) {
            brotherLabel.setVisible(false);
          }
        });
  }

  /** method for toggling the HBox */
  private void toggleHorizontalBox() {
    // Create the transition
    TranslateTransition transition =
        new TranslateTransition(Duration.seconds(0.5), chatHorizontalBox);

    if (!chatHorizontalBox.isVisible()) {
      chatHorizontalBox.setVisible(true); // Show before animation
      transition.setFromY(chatHorizontalBox.getHeight() + 50); // Start off-screen
      transition.setToY(0); // Move to visible position
    }

    transition
        .onFinishedProperty()
        .set(
            e -> {
              explanationLabel.setVisible(true);
            });

    // Play the transition
    transition.play();
  }

  @SuppressWarnings("static-access")
  public void setContext(GameStateContext context) {
    this.context = context;
  }

  public void styleEndOfGame() {
    // shows and hides various UI elements when the game ends
    brotherImage.setVisible(false);
    workerImage.setVisible(false);
    ownerImage.setVisible(false);
    restartButton.setVisible(true);
    guessTextArea.setVisible(true);
    textInput.setVisible(false);
    statsPane.setVisible(false);
    buttonSend.setVisible(false);
    lblDescription.setVisible(false);
    explanationLabel.setVisible(false);
    ownerLabel.setVisible(false);
    workerLabel.setVisible(false);
    brotherLabel.setVisible(false);
  }

  @FXML
  void onRestartButtonClicked(ActionEvent event) {
    TimelineManager.initialiseTimeLine();
    CrimeSceneController.setContext(new GameStateContext());
    Platform.runLater(
        () -> {
          Scene scene = restartButton.getScene();
          scene.setRoot(SceneManager.getRoot(SceneManager.Scene.START));
          FXMLLoader phoneLoader = new FXMLLoader(App.class.getResource("/fxml/phone.fxml"));
          FXMLLoader newspaperLoader =
              new FXMLLoader(App.class.getResource("/fxml/newspaper.fxml"));
          FXMLLoader cctvLoader = new FXMLLoader(App.class.getResource("/fxml/cctv.fxml"));
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
            SceneManager.addRoot(SceneManager.Scene.CCTV, cctvLoader.load());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          // Set the root of the scene to the start scene
          SceneManager.setPhoneLoader(phoneLoader);
          SceneManager.setCameraLoader(cctvLoader);
          SceneManager.setNewspaperLoader(newspaperLoader);

          timeForGuessing = 60000;
        });
  }
}
