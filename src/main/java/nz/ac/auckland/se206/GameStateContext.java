package nz.ac.auckland.se206;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.states.GameOver;
import nz.ac.auckland.se206.states.GameStarted;
import nz.ac.auckland.se206.states.GameState;
import nz.ac.auckland.se206.states.Guessing;
import org.yaml.snakeyaml.Yaml;

/**
 * Context class for managing the state of the game. Handles transitions between different game
 * states and maintains game data such as the professions and rectangle IDs.
 */
public class GameStateContext {

  private final String rectIdToGuess;
  private final Person personToGuess;
  private final Map<String, Person> rectanglesToProfession;
  private final GameStarted gameStartedState;
  private final Guessing guessingState;
  private final GameOver gameOverState;
  private GameState gameState;
  private RoomController roomController;

  /** Constructs a new GameStateContext and initializes the game states and professions. */
  public GameStateContext() {
    gameStartedState = new GameStarted(this);
    guessingState = new Guessing(this);
    gameOverState = new GameOver(this);

    gameState = gameStartedState; // Initial state
    Map<String, Object> professionMap = null;
    Map<String, Object> nameMap = null;
    Map<String, Object> roleMap = null;
    Map<String, Object> colorMap = null;

    Yaml yaml = new Yaml();
    try (InputStream professionInputStream =
        GameStateContext.class.getClassLoader().getResourceAsStream("data/professions.yaml")) {
      if (professionInputStream == null) {
        throw new IllegalStateException("File not found!");
      }
      professionMap = yaml.load(professionInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (InputStream roleInputStream =
        GameStateContext.class.getClassLoader().getResourceAsStream("data/roles.yaml")) {
      if (roleInputStream == null) {
        throw new IllegalStateException("File not found!");
      }
      roleMap = yaml.load(roleInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (InputStream nameInputStream =
        GameStateContext.class.getClassLoader().getResourceAsStream("data/names.yaml")) {
      if (nameInputStream == null) {
        throw new IllegalStateException("File not found!");
      }
      nameMap = yaml.load(nameInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (InputStream colorInputStream =
        GameStateContext.class.getClassLoader().getResourceAsStream("data/colors.yaml")) {
      if (colorInputStream == null) {
        throw new IllegalStateException("File not found!");
      }
      colorMap = yaml.load(colorInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    @SuppressWarnings("unchecked")
    List<String> professions = (List<String>) professionMap.get("professions");
    @SuppressWarnings("unchecked")
    List<String> names = (List<String>) nameMap.get("names");
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) roleMap.get("roles");
    @SuppressWarnings("unchecked")
    List<String> colors = (List<String>) colorMap.get("colors");

    Random random = new Random();
    Set<String> randomProfessions = new HashSet<>();
    while (randomProfessions.size() < 3) {
      String profession = professions.get(random.nextInt(professions.size()));
      randomProfessions.add(profession);
    }

    Set<String> randomNames = new HashSet<>();
    while (randomNames.size() < 3) {
      String name = names.get(random.nextInt(names.size()));
      randomNames.add(name);
    }

    Set<String> randomRoles = new HashSet<>();
    while (randomRoles.size() < 3) {
      String role = roles.get(random.nextInt(roles.size()));
      randomRoles.add(role);
    }

    Set<String> randomColor = new HashSet<>();
    while (randomColor.size() < 3) {
      String color = colors.get(random.nextInt(colors.size()));
      randomColor.add(color);
    }

    String[] randomProfessionsArray = randomProfessions.toArray(new String[3]);
    String[] randomNamesArray = randomNames.toArray(new String[3]);
    String[] randomRolesArray = randomRoles.toArray(new String[3]);
    int randomIndex = random.nextInt(3);
    randomRolesArray[randomIndex] = "thief";

    Person person1 =
        new Person(
            randomNamesArray[0],
            randomRolesArray[0],
            randomProfessionsArray[0],
            randomColor.toArray(new String[3])[0]);
    Person person2 =
        new Person(
            randomNamesArray[1],
            randomRolesArray[1],
            randomProfessionsArray[1],
            randomColor.toArray(new String[3])[1]);
    Person person3 =
        new Person(
            randomNamesArray[2],
            randomRolesArray[2],
            randomProfessionsArray[2],
            randomColor.toArray(new String[3])[2]);

    rectanglesToProfession = new HashMap<>();
    rectanglesToProfession.put("rectPerson1", person1);
    rectanglesToProfession.put("rectPerson2", person2);
    rectanglesToProfession.put("rectPerson3", person3);

    rectIdToGuess =
        randomIndex == 0 ? "rectPerson1" : ((randomIndex == 1) ? "rectPerson2" : "rectPerson3");
    personToGuess = rectanglesToProfession.get(rectIdToGuess);
  }

  /**
   * Sets the current state of the game.
   *
   * @param state the new state to set
   */
  public void setState(GameState state) {
    this.gameState = state;
  }

  /**
   * Gets the initial game started state.
   *
   * @return the game started state
   */
  public GameState getGameStartedState() {
    return gameStartedState;
  }

  /**
   * Gets the guessing state.
   *
   * @return the guessing state
   */
  public GameState getGuessingState() {
    return guessingState;
  }

  /**
   * Gets the game over state.
   *
   * @return the game over state
   */
  public GameState getGameOverState() {
    return gameOverState;
  }

  /**
   * Gets the profession to be guessed.
   *
   * @return the profession to guess
   */
  public Person getPersonToGuess() {
    return personToGuess;
  }

  /**
   * Gets the ID of the rectangle to be guessed.
   *
   * @return the rectangle ID to guess
   */
  public String getRectIdToGuess() {
    return rectIdToGuess;
  }

  /**
   * Gets the profession associated with a specific rectangle ID.
   *
   * @param rectangleId the rectangle ID
   * @return the profession associated with the rectangle ID
   */
  public Person getPerson(String rectangleId) {
    return rectanglesToProfession.get(rectangleId);
  }

  /**
   * Handles the event when a rectangle is clicked.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    gameState.handleRectangleClick(event, rectangleId);
  }

  /**
   * Handles the event when the guess button is clicked.
   *
   * @throws IOException if there is an I/O error
   */
  public void handleGuessClick() throws IOException {
    gameState.handleGuessClick();
  }

  public void handleTrashBinClick(MouseEvent event, String itemId) throws IOException {
    gameState.handleTrashBinClick(event, itemId);
  }

  public void handleCameraClick(MouseEvent event, String itemId) throws IOException {
    gameState.handleCameraClick(event, itemId);
  }

  public void handleCarClick(MouseEvent event, String itemId) throws IOException {
    gameState.handleCarClick(event, itemId);
  }

  public void setRoomController(RoomController roomController) {
    this.roomController = roomController;
  }

  public RoomController getRoomController() {
    return roomController;
  }

  public GameState getGameState() {
    return gameState;
  }
}
