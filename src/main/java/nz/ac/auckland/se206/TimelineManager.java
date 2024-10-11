package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import nz.ac.auckland.se206.controllers.CrimeSceneController;

public class TimelineManager {
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static Timeline timeline;
  private static GameStateContext context;

  /** This method initialises the timeline */
  public static void initialiseTimeLine() {
    timeToCount = 300000;
    timeline = new Timeline();
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                // timeline counts down in milliseconds so we can be very accurate when passing time
                // between scenes
                Duration.millis(1),
                event -> {
                  if (timeToCount > 0) {
                    timeToCount--;
                    // set progress
                    progress = (int) (100 - ((timeToCountTo - timeToCount) * 100 / timeToCountTo));
                  } else {
                    // check scene switching condiitions when timer is left to run out
                    Utils.checkConditions(
                        context,
                        context.isAllSuspectsSpokenTo(),
                        CrimeSceneController.isAnyClueFound(),
                        timeline);
                    timeline.stop();
                  }
                }));
    // timer cycles indefinitely unless actively stopped
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  /**
   * This method sets the time to count
   *
   * @param timeFromPreviousScene
   */
  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  /**
   * This method gets the time to count
   *
   * @return
   */
  public static double getTimeToCount() {
    return timeToCount;
  }

  /**
   * This method sets the progress
   *
   * @param progress
   */
  public static void setProgress(int progress) {
    TimelineManager.progress = progress;
  }

  /**
   * This method gets the progress
   *
   * @return
   */
  public static int getProgress() {
    return progress;
  }

  /** This method starts the timer */
  public static void startTimer() {
    timeline.play();
  }

  /** This method stops the timer */
  public static void stopTimer() {
    timeline.stop();
  }

  /**
   * sets the context
   *
   * @param context
   */
  public static void setContext(GameStateContext context) {
    TimelineManager.context = context;
  }
}
