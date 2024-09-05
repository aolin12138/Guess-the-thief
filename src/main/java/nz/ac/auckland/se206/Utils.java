package nz.ac.auckland.se206;

/*Utility methods for common tasks. Feel free to add static methods here. */
public class Utils {

  /**
   * Converts the given time in seconds to a formatted time string in the format "MM:SS". The input
   * seconds represent the time remaining, and the method calculates the time used from a total of
   * 660 seconds (600 seconds gameplay + 60 seconds to guess).
   *
   * @param seconds the time remaining in seconds
   * @return a formatted time string in the format "MM:SS"
   */
  public static String convertSecondsToTimeFormat(int seconds) {
    seconds =
        660 - seconds; // Calculate time used from time remaining (600 seconds gameplay + 60 seconds
    // to guess)
    int minutes = seconds / 60;
    int remainingSeconds = seconds - (minutes * 60);

    // Formatting
    String minuteAppend = (minutes < 10) ? "0" : "";
    String secondAppend = (remainingSeconds < 10) ? "0" : "";

    System.out.println((minuteAppend + minutes + ":" + secondAppend + remainingSeconds + " "));

    return (minuteAppend + minutes + ":" + secondAppend + remainingSeconds + " ");
  }
}
