package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml". Had to set visibililty to public for the StartController
   * -> On enter clicked, Crime scene must be initialised over there because timer starts on
   * initialisation.
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  public static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // Store the stage in the SceneManager so it will be remembered

    FXMLLoader startLoader = new FXMLLoader(App.class.getResource("/fxml/start.fxml"));
    FXMLLoader roomLoader = new FXMLLoader(App.class.getResource("/fxml/room.fxml"));
    FXMLLoader instructionLoader = new FXMLLoader(App.class.getResource("/fxml/instructions.fxml"));
    FXMLLoader phoneLoader = new FXMLLoader(App.class.getResource("/fxml/phone.fxml"));
    FXMLLoader newspaperLoader = new FXMLLoader(App.class.getResource("/fxml/newspaper.fxml"));
    FXMLLoader callHistoryLoader = new FXMLLoader(App.class.getResource("/fxml/callhistory.fxml"));
    FXMLLoader cctvLoader = new FXMLLoader(App.class.getResource("/fxml/cctv.fxml"));

    // Load the fxml files and add the roots to the SceneManager
    SceneManager.addRoot(SceneManager.Scene.INSTRUCTIONS, instructionLoader.load());
    SceneManager.addRoot(SceneManager.Scene.START, startLoader.load());
    SceneManager.addRoot(SceneManager.Scene.ROOM, roomLoader.load());
    SceneManager.addRoot(SceneManager.Scene.PHONE, phoneLoader.load());
    SceneManager.addRoot(SceneManager.Scene.NEWSPAPER, newspaperLoader.load());
    SceneManager.addRoot(SceneManager.Scene.CALL_HISTORY, callHistoryLoader.load());
    SceneManager.addRoot(SceneManager.Scene.CCTV, cctvLoader.load());
    // Set the loaders in the SceneManager
    SceneManager.setRoomLoader(roomLoader);
    SceneManager.setPhoneLoader(phoneLoader);
    SceneManager.setCameraLoader(cctvLoader);
    SceneManager.setCallHistoryLoader(callHistoryLoader);
    SceneManager.setNewspaperLoader(newspaperLoader);

    // set the timeline manager to the initial timeline.
    TimelineManager.initialiseTimeLine();

    scene = new Scene(SceneManager.getRoot(SceneManager.Scene.START));
    stage.setScene(scene);

    // Set the title of the stage
    stage.setTitle("PI Masters: Detective Training");
    stage.show();
    SceneManager.getRoot(SceneManager.Scene.START).requestFocus();
  }
}
