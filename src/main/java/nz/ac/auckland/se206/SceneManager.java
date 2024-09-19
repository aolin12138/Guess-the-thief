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

  public static void addRoot(Scene scene, Parent root) {
    sceneMap.put(scene, root);
  }

  public static Parent getRoot(Scene scene) {
    return sceneMap.get(scene);
  }

  public static void setPhoneLoader(FXMLLoader phoneLoader) {
    SceneManager.phoneLoader = phoneLoader;
  }

  public static void setCCTVLoader(FXMLLoader cctvLoader) {
    SceneManager.cctvLoader = cctvLoader;
  }

  public static void setNewspaperLoader(FXMLLoader newspaperLoader) {
    SceneManager.newspaperLoader = newspaperLoader;
  }

  public static void setRoomLoader(FXMLLoader roomLoader) {
    SceneManager.roomLoader = roomLoader;
  }

  public static void setCallHistoryLoader(FXMLLoader callhistoryLoader) {
    SceneManager.callhistoryLoader = callhistoryLoader;
  }

  public static void setCrimeSceneLoader(FXMLLoader crimeSceneLoader) {
    SceneManager.crimeSceneLoader = crimeSceneLoader;
  }

  public static FXMLLoader getRoomLoader() {
    return roomLoader;
  }

  public static FXMLLoader getPhoneLoader() {
    return phoneLoader;
  }

  public static FXMLLoader getCallHistoryLoader() {
    return callhistoryLoader;
  }

  public static FXMLLoader getCCTVLoader() {
    return cctvLoader;
  }

  public static FXMLLoader getNewspaperLoader() {
    return newspaperLoader;
  }

  public static FXMLLoader getCrimeSceneLoader() {
    return crimeSceneLoader;
  }
}
