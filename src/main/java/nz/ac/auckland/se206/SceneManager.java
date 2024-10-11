package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/** This class is used to manage the scenes in the game. */
public class SceneManager {
  /** The different scenes in the game. */
  public enum Scene {
    INSTRUCTIONS,
    START,
    CRIME,
    ROOM,
    Chat,
    GUESS,
    NEWSPAPER,
    PHONE,
    CALL_HISTORY,
    CCTV
  }

  public static FXMLLoader roomLoader;
  public static FXMLLoader crimeSceneLoader;
  public static FXMLLoader phoneLoader;
  public static FXMLLoader cctvLoader;
  public static FXMLLoader newspaperLoader;
  public static FXMLLoader callhistoryLoader;

  private static HashMap<Scene, Parent> sceneMap = new HashMap<Scene, Parent>();

  /**
   * Adds the root of the scene to the sceneMap.
   *
   * @param scene the scene
   * @param root the root of the scene
   */
  public static void addRoot(Scene scene, Parent root) {
    sceneMap.put(scene, root);
  }

  /**
   * Gets the root of the scene.
   *
   * @param scene the scene
   * @return the root of the scene
   */
  public static Parent getRoot(Scene scene) {
    return sceneMap.get(scene);
  }

  /**
   * Sets the phone loader for usage.
   *
   * @param phoneLoader the FXMLLoader to set for the phone loader in the game.
   */
  public static void setPhoneLoader(FXMLLoader phoneLoader) {
    SceneManager.phoneLoader = phoneLoader;
  }

  /**
   * Sets the camera loader for usage in the game.
   *
   * @param cctvLoader the FXMLLoader to set for the camera loader in the game.
   */
  public static void setCameraLoader(FXMLLoader cctvLoader) {
    SceneManager.cctvLoader = cctvLoader;
  }

  /**
   * Sets the newspaper loader for usage in the game.
   *
   * @param newspaperLoader the FXMLLoader to set for the newspaper.
   */
  public static void setNewspaperLoader(FXMLLoader newspaperLoader) {
    SceneManager.newspaperLoader = newspaperLoader;
  }

  /**
   * Sets the room loader for usage in the game.
   *
   * @param roomLoader the FXMLLoader to set for the room.
   */
  public static void setRoomLoader(FXMLLoader roomLoader) {
    SceneManager.roomLoader = roomLoader;
  }

  /**
   * Sets the call history loader for usage in the game.
   *
   * @param callhistoryLoader the FXMLLoader to set for the call history.
   */
  public static void setCallHistoryLoader(FXMLLoader callhistoryLoader) {
    SceneManager.callhistoryLoader = callhistoryLoader;
  }

  /**
   * Sets the crime scene loader.
   *
   * @param crimeSceneLoader Adding more words due to requirements for description.
   */
  public static void setCrimeSceneLoader(FXMLLoader crimeSceneLoader) {
    SceneManager.crimeSceneLoader = crimeSceneLoader;
  }

  /**
   * Gets the room loader for usage.
   *
   * @return the FXMLLoader for the room
   */
  public static FXMLLoader getRoomLoader() {
    return roomLoader;
  }

  /**
   * Gets the phone loader for usage.
   *
   * @return the FXMLLoader for the phone
   */
  public static FXMLLoader getPhoneLoader() {
    return phoneLoader;
  }

  /**
   * Gets the call history loader for usage.
   *
   * @return the FXMLLoader for the call history
   */
  public static FXMLLoader getCallHistoryLoader() {
    return callhistoryLoader;
  }

  /**
   * Gets the camera loader for usage.
   *
   * @return the FXMLLoader for the camera
   */
  public static FXMLLoader getCameraLoader() {
    return cctvLoader;
  }

  /**
   * Gets the newspaper loader for usage.
   *
   * @return the FXMLLoader for the newspaper
   */
  public static FXMLLoader getNewspaperLoader() {
    return newspaperLoader;
  }

  /**
   * Gets the crime scene loader for usage.
   *
   * @return the FXMLLoader for the crime scene
   */
  public static FXMLLoader getCrimeSceneLoader() {
    return crimeSceneLoader;
  }
}
