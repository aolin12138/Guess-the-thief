package nz.ac.auckland.se206;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*Utility methods for common tasks. Feel free to add static methods here. */
public class Utils {

  public static Scanner scanner = new Scanner(System.in);
  public static FileWriter writer;

  private static ArrayList<String> previousScores = new ArrayList<String>();

  public static void writeToCsv(String user, String time) throws IOException {
    try {
      writer = new FileWriter("./src/main/resources/csv/previous_rounds.csv", true);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    writer.append(user + "," + time + "\n");
    writer.close();
  }

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

  // Used to compare the previous rounds scores against the new score (both must be integers)
  public static String convertTimeFormatToSeconds(String timeString) {
    String[] time = timeString.split(":");
    int minutes = Integer.parseInt(time[0]);
    int seconds = Integer.parseInt(time[1]);
    // 660 - time because we are comparing against the time remaining, NOT the time used
    return Integer.toString(660 - ((minutes * 60) + seconds));
  }

  // This method will be called in the Finished game controller.
  // If the player won, their score will be passed here and checked to see if it is in the top 3.
  // If it is, this method will sort the csv files to include the new score and in the correct
  // order.
  public static void updateScoreBoard(String user, int time) {

    // Read current scores from the csv file
    String element;
    // no need to close the BufferedReader, will be close automaticsally because is inside the try
    try (BufferedReader br =
        new BufferedReader(new FileReader("./src/main/resources/csv/previous_rounds.csv"))) {
      while ((element = br.readLine()) != null) {
        previousScores.add(element);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1); // exit the program
    }

    // Now must compare each time in the arraylist to the new time and see if the new time is faster
    if (previousScores.isEmpty()) {
      System.out.println("hello world");
      // Write the new score to the csv file
      // writer.println(user + "," +
      // convertSecondsToTimeFormat(time));***************************************

      return;
    } else {
      int currentRank = 4;
      int j = 1;
      int i = previousScores.size() - 1;
      // Iterate backwards through the arraylist since the slowest value will be at the end
      while (i > 0) {
        if (time < Integer.parseInt(convertTimeFormatToSeconds(previousScores.get(i)))) {
          currentRank = currentRank - j;

        } else {
          // Compare against the next time
          i = i - 2;
        }
      }
    }
  }
}
