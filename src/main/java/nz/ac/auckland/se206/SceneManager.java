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

  private static HashMap<Scene, Parent> sceneMap = new HashMap<Scene, Parent>();

  public static void addRoot(Scene scene, Parent root) {
    sceneMap.put(scene, root);
  }

  public static Parent getRoot(Scene scene) {
    return sceneMap.get(scene);
  }

  public static void setRoomLoader(FXMLLoader roomLoader) {
    SceneManager.roomLoader = roomLoader;
  }

  public static void setCrimeSceneLoader(FXMLLoader crimeSceneLoader) {
    SceneManager.crimeSceneLoader = crimeSceneLoader;
  }

  public static FXMLLoader getRoomLoader() {
    return roomLoader;
  }

  public static FXMLLoader getCrimeSceneLoader() {
    return crimeSceneLoader;
  }
}
