package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {
  public enum Scene {
    INSTRUCTIONS,
    START,
    CRIME,
    ROOM,
    Chat,
    NEWSPAPER,
    PHONE,
    CALL_HISTORY
  }

  private static HashMap<Scene, Parent> sceneMap = new HashMap<Scene, Parent>();

  public static void addRoot(Scene scene, Parent root) {
    sceneMap.put(scene, root);
  }

  public static Parent getRoot(Scene scene) {
    return sceneMap.get(scene);
  }
}
