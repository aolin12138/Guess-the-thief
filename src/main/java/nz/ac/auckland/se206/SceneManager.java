package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneManager {
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
   * Sets the phone loader.
   *
   * @param phoneLoader
   */
  public static void setPhoneLoader(FXMLLoader phoneLoader) {
    SceneManager.phoneLoader = phoneLoader;
  }

  /**
   * Sets the camera loader.
   *
   * @param cctvLoader
   */
  public static void setCameraLoader(FXMLLoader cctvLoader) {
    SceneManager.cctvLoader = cctvLoader;
  }

  /**
   * Sets the newspaper loader.
   *
   * @param newspaperLoader
   */
  public static void setNewspaperLoader(FXMLLoader newspaperLoader) {
    SceneManager.newspaperLoader = newspaperLoader;
  }

  /**
   * Sets the room loader.
   *
   * @param roomLoader
   */
  public static void setRoomLoader(FXMLLoader roomLoader) {
    SceneManager.roomLoader = roomLoader;
  }

  /**
   * Sets the call history loader.
   *
   * @param callhistoryLoader
   */
  public static void setCallHistoryLoader(FXMLLoader callhistoryLoader) {
    SceneManager.callhistoryLoader = callhistoryLoader;
  }

  /**
   * Sets the crime scene loader.
   *
   * @param crimeSceneLoader
   */
  public static void setCrimeSceneLoader(FXMLLoader crimeSceneLoader) {
    SceneManager.crimeSceneLoader = crimeSceneLoader;
  }

  /**
   * Gets the room loader.
   *
   * @return roomLoader
   */
  public static FXMLLoader getRoomLoader() {
    return roomLoader;
  }

  /**
   * Gets the phone loader.
   *
   * @return
   */
  public static FXMLLoader getPhoneLoader() {
    return phoneLoader;
  }

  /**
   * Gets the call history loader.
   *
   * @return
   */
  public static FXMLLoader getCallHistoryLoader() {
    return callhistoryLoader;
  }

  /**
   * Gets the camera loader.
   *
   * @return
   */
  public static FXMLLoader getCameraLoader() {
    return cctvLoader;
  }

  /**
   * Gets the newspaper loader.
   *
   * @return
   */
  public static FXMLLoader getNewspaperLoader() {
    return newspaperLoader;
  }

  /**
   * Gets the crime scene loader.
   *
   * @return
   */
  public static FXMLLoader getCrimeSceneLoader() {
    return crimeSceneLoader;
  }
}
