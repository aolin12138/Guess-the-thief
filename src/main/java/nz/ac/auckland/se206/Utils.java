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
  private static ArrayList<String> previousScoresNames = new ArrayList<String>();
  private static ArrayList<String> previousScoresTimes = new ArrayList<String>();

  /**
   * Writes the user's name and time to a CSV file. The data is appended to the end of any current
   * data in the file located at "./src/main/resources/csv/previous_rounds.csv". This is used to
   * store the previous rounds scores to be displayed in the scoreboard on the start page.
   *
   * @param user the name of the user
   * @param time the time to be recorded in the CSV file
   * @throws IOException if an I/O error occurs
   */
  public static void writeToCsv(String user, String time, boolean append) throws IOException {
    try {
      writer = new FileWriter("./src/main/resources/csv/previous_rounds.csv", append);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    writer.append(user + "," + time + "\n");
    writer.close();
  }

  /**
   * Reads the previous rounds' scores from a CSV file located at
   * "./src/main/resources/csv/previous_rounds.csv". The method reads each line of the file, splits
   * the line by commas, and adds the user's name and time to the respective lists:
   * previousScoresNames and previousScoresTimes.
   *
   * @throws IOException if an I/O error occurs during reading the file
   */
  public static void readCsv() {
    String element;
    int line = 0;
    try (BufferedReader br =
        new BufferedReader(new FileReader("./src/main/resources/csv/previous_rounds.csv"))) {
      while (((element = br.readLine()) != null) && (line < 3)) {
        // skip empty lines in csv
        if (element.trim().isEmpty()) {
          continue;
        }
        String[] split = element.split(",");
        previousScoresNames.add(split[0]);
        previousScoresTimes.add(split[1]);
        line++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1); // exit the program
    }
  }

  /**
   * Converts the given time in seconds to a formatted time string in the format "MM:SS". The input
   * seconds represent the time remaining, and the method calculates the time used from a total of
   * 360 seconds (600 seconds gameplay + 60 seconds to guess).
   *
   * @param seconds the time remaining in seconds
   * @return a formatted time string in the format "MM:SS"
   */
  public static String convertSecondsToTimeFormat(int seconds) {
    seconds =
        360 - seconds; // Calculate time used from time remaining (600 seconds gameplay + 60 seconds
    // to guess)
    int minutes = seconds / 60;
    int remainingSeconds = seconds - (minutes * 60);
    // Formatting
    String minuteAppend = (minutes < 10) ? "0" : "";
    String secondAppend = (remainingSeconds < 10) ? "0" : "";
    return (minuteAppend + minutes + ":" + secondAppend + remainingSeconds + " ");
  }

  /**
   * Converts a formatted time string in the format "MM:SS" to the total time in seconds. The input
   * time string represents the time used, and the method calculates the time remaining from a total
   * of 360 seconds (600 seconds gameplay + 60 seconds to guess).
   *
   * @param time the formatted time string in the format "MM:SS"
   * @return the total time in seconds
   * @throws IllegalArgumentException if the time format is invalid
   */
  public static int convertTimeFormatToSeconds(String timeString) {
    String[] time = timeString.split(":");
    if (time.length != 2) {
      throw new IllegalArgumentException("Invalid time format. Expected format is MM:SS.");
    }
    int minutes = Integer.parseInt(time[0].trim());
    int seconds = Integer.parseInt(time[1].trim());
    // 360 - time because we are comparing against the time remaining, NOT the time used
    return (360 - ((minutes * 60) + seconds));
  }

  // This method will be called in the Finished game controller.
  // If the player won, their score will be passed here and checked to see if it is in the top 3.
  // If it is, this method will sort the csv files to include the new score and in the correct
  // order.
  public static void updateScoreBoard(String user, int time) {
    // Read current scores from the csv file
    previousScoresNames.clear();
    previousScoresTimes.clear();
    readCsv();
    // If the arraylist is empty, write the new score to the csv file
    if (previousScoresNames.isEmpty()) {
      try {
        previousScoresTimes.add(0, convertSecondsToTimeFormat(time));
        previousScoresNames.add(0, user);
        writeToCsv(user, convertSecondsToTimeFormat(time), false);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    } else if (previousScoresNames.size() == 1) {
      // need to sort it first
      if (time > convertTimeFormatToSeconds(previousScoresTimes.get(0))) {
        previousScoresTimes.add(1, previousScoresTimes.get(0));
        previousScoresNames.add(1, previousScoresNames.get(0));
        previousScoresTimes.set(0, convertSecondsToTimeFormat(time));
        previousScoresNames.set(0, user);
        appendToCsv(2);
        return;
      } else {
        previousScoresTimes.add(1, convertSecondsToTimeFormat(time));
        previousScoresNames.add(1, user);
        appendToCsv(2);
        return;
      }
    } else if (previousScoresNames.size() == 2) {
      // need to sort it first
      if (time > convertTimeFormatToSeconds(previousScoresTimes.get(0))) {
        previousScoresTimes.add(2, previousScoresTimes.get(1));
        previousScoresNames.add(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, previousScoresTimes.get(0));
        previousScoresNames.set(1, previousScoresNames.get(0));
        previousScoresTimes.set(0, convertSecondsToTimeFormat(time));
        previousScoresNames.set(0, user);
        appendToCsv(3);
        return;
      } else if (time > convertTimeFormatToSeconds(previousScoresTimes.get(1))) {
        previousScoresTimes.add(2, previousScoresTimes.get(1));
        previousScoresNames.add(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, convertSecondsToTimeFormat(time));
        previousScoresNames.set(1, user);
        appendToCsv(3);
        return;
      } else {
        previousScoresTimes.add(2, convertSecondsToTimeFormat(time));
        previousScoresNames.add(2, user);
        appendToCsv(3);
        return;
      }
    }

    // Now must compare each time in the arraylist to the new time and see if the new time is faster
    if (time <= convertTimeFormatToSeconds(previousScoresTimes.get(2))) {
      // the current round time is slower than the slowest scoreboard time, we do not change the
      // scoreboard
      return;
    }
    for (int i = 0; i < previousScoresTimes.size(); i++) {
      if ((time > convertTimeFormatToSeconds(previousScoresTimes.get(i))) && i == 0) {
        // the current round time is a new record

        previousScoresTimes.set(2, previousScoresTimes.get(1));
        previousScoresNames.set(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, previousScoresTimes.get(0));
        previousScoresNames.set(1, previousScoresNames.get(0));
        previousScoresTimes.set(0, convertSecondsToTimeFormat(time));
        previousScoresNames.set(0, user);
        appendToCsv(3);
        return;
      } else if ((time > convertTimeFormatToSeconds(previousScoresTimes.get(i))) && i == 1) {
        previousScoresTimes.set(2, previousScoresTimes.get(1));
        previousScoresNames.set(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, convertSecondsToTimeFormat(time));
        previousScoresNames.set(1, user);
        appendToCsv(3);
        return;
      } else if ((time > convertTimeFormatToSeconds(previousScoresTimes.get(i))) && i == 2) {
        previousScoresTimes.set(2, convertSecondsToTimeFormat(time));
        previousScoresNames.set(2, user);
        appendToCsv(3);
        return;
      }
    }
  }

  /**
   * Appends the top scores to the CSV file. The method iterates through the top scores and writes
   * them to the CSV file located at "./src/main/resources/csv/previous_rounds.csv". The first score
   * overwrites the existing file, and subsequent scores are appended.
   *
   * @param jmax the number of top scores to write to the CSV file
   */
  public static void appendToCsv(int jmax) {
    for (int j = 0; j < jmax; j++) {
      if (j == 0) {
        try {
          // overwrite csv
          writeToCsv(previousScoresNames.get(j), previousScoresTimes.get(j), false);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        try {
          // csv already overwritten, set append to true
          writeToCsv(previousScoresNames.get(j), previousScoresTimes.get(j), true);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Returns the list of scoreboard names.
   *
   * @return an ArrayList of scoreboard names
   */
  public ArrayList<String> getScoresNames() {
    return previousScoresNames;
  }

  /**
   * Returns the list of scoreboard times.
   *
   * @return an ArrayList of scoreboard times
   */
  public ArrayList<String> getScoresTimes() {
    return previousScoresTimes;
  }
}
