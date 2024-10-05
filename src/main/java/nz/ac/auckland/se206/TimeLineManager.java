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

  public static void initialiseTimeLine() {
    timeline = new Timeline();
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.millis(1),
                event -> {
                  if (timeToCount > 0) {
                    timeToCount--;
                    progress = (int) (100 - ((timeToCountTo - timeToCount) * 100 / timeToCountTo));
                  } else {
                    Utils.checkConditions(
                        context,
                        context.isAllSuspectsSpokenTo(),
                        CrimeSceneController.isAnyClueFound(),
                        timeline);
                    timeline.stop();
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  public static void setTimeToCount(double timeFromPreviousScene) {
    timeToCount = timeFromPreviousScene;
  }

  public static double getTimeToCount() {
    return timeToCount;
  }

  public static void setProgress(int progress) {
    TimelineManager.progress = progress;
  }

  public static int getProgress() {
    return progress;
  }

  public static void startTimer() {
    timeline.play();
  }

  public static void stopTimer() {
    timeline.stop();
  }

  public static void setContext(GameStateContext context) {
    TimelineManager.context = context;
  }
}
