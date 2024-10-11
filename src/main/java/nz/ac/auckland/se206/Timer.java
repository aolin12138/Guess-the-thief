package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Timer {

  private static double timeToCountTo = 300000;
  private static double timeToCount = 0;
  private static double guessTime = 60000;
  private static int progress = 100;

  @SuppressWarnings("unused")
  private static boolean isTimeOver = false;

  private static Timeline timeline = new Timeline();

  /**
   * Starts the countdown timer. Extra tag description due missing tag description. Extra tag
   * description due missing tag description.
   */
  public static void startTimer() {
    // create a new timeline
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                // event handler for the timeline
                event -> {
                  if (timeToCount > 0) {
                    timeToCount--;
                    // calculate the progress of the timer
                    progress = (int) (100 - ((timeToCountTo - timeToCount) * 100 / timeToCountTo));
                    System.out.println(progress);
                    if ((timeToCount % 1000) == 0) {
                      System.out.println(timeToCount);
                    }
                  } else {
                    // reconfigure the timer variables for the guessing timer
                    timeline.stop();
                    timeToCountTo = guessTime;
                    timeToCount = 0;
                    progress = 100;
                  }
                }));
    // set the cycle count of the timeline to indefinite
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /**
   * Sets the time to count to a specified number of seconds.
   *
   * @param seconds Extra tag description due missing tag description.
   */
  public static void setTimeToCountTo(int seconds) {
    timeToCountTo = seconds;
  }

  /**
   * Sets the time to count to a specified number of seconds for guessing
   *
   * @param seconds Extra tag description due missing tag description.
   */
  public static void setGuessTime(int seconds) {
    guessTime = seconds;
  }

  /**
   * Gets the current time remaining in the countdown timer.
   *
   * @return the current time remaining in milliseconds
   */
  public static double getTime() {
    return timeToCount;
  }

  /**
   * Gets the current progress of the timer.
   *
   * @return the current progress as an integer
   */
  public static int getProgress() {
    return progress;
  }

  public static void startGuessTimer() {}

  /** Resets the timer to its initial state. Call this method when needing to restart the game. */
  public static void resetTimer() {
    timeToCountTo = 300000;
    timeToCount = 0;
    guessTime = 60000;
    progress = 100;
    isTimeOver = false;
  }
}
