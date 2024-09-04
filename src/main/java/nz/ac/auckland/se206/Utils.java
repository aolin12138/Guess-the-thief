package nz.ac.auckland.se206;

/*Utility methods for common tasks. Feel free to add static methods here. */
public class Utils {

  public static String convertSecondsToTimeFormat(int seconds) {
    seconds =
        660
            - seconds; // Calculate time used from time remaining (600 seconds gameplay + 60 seconds
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
