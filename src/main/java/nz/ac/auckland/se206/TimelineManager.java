package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import nz.ac.auckland.se206.controllers.CrimeSceneController;

/** This class manages the timeline for the game. */
public class TimelineManager {
  private static double timeToCount = 300000;
  private static double timeToCountTo = 300000;
  private static int progress = 0;
  private static Timeline timeline;
  private static GameStateContext context;

  /** This method initialises the timeline. */
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
   * Sets the time to count.
   *
   * @param timeFromPreviousScene the time to set for counting, typically from the previous scene
   */
  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  /**
   * Gets the time to count.
   *
   * @return the time to count as a double
   */
  public static double getTimeToCount() {
    return timeToCount;
  }

  /**
   * This method sets the progress for usage.
   *
   * @param progress Adding extra due missing requirements in methods.
   */
  public static void setProgress(int progress) {
    TimelineManager.progress = progress;
  }

  /**
   * Gets the current progress Adding extra due missing requirements in methods.
   *
   * @return the current progress as an integer.
   */
  public static int getProgress() {
    return progress;
  }

  /**
   * Starts the timer.
   *
   * <p>This method plays the timeline, effectively starting the timer.
   */
  public static void startTimer() {
    timeline.play();
  }

  /** This method stops the timer. */
  public static void stopTimer() {
    timeline.stop();
  }

  /**
   * Sets the game state context.
   *
   * @param context the GameStateContext to set
   */
  public static void setContext(GameStateContext context) {
    TimelineManager.context = context;
  }
}
