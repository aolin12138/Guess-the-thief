package nz.ac.auckland.se206;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.Timeline;
import nz.ac.auckland.se206.controllers.CrimeSceneController;
import nz.ac.auckland.se206.controllers.GameOverController;

/*Utility methods for common tasks. Feel free to add static methods here. */
public class Utils {

  public static Scanner scanner = new Scanner(System.in);
  public static FileWriter writer;
  private static ArrayList<String> previousScoresNames = new ArrayList<String>();
  private static ArrayList<String> previousScoresTimes = new ArrayList<String>();
  private static String playerName;
  private static double timeUsed = 0;

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
  public static void updateScoreBoard(int time, String playerName) {
    // Read current scores from the csv file
    System.out.println("First: " + previousScoresNames.size());

    previousScoresNames.clear();
    previousScoresTimes.clear();
    readCsv();
    // If the arraylist is empty, write the new score to the csv file
    if (previousScoresNames.isEmpty()) {
      System.out.println("Second: " + previousScoresNames.size());

      System.out.println("Hello world");
      try {
        previousScoresTimes.add(0, convertSecondsToTimeFormat(time));
        previousScoresNames.add(0, playerName);
        writeToCsv(playerName, convertSecondsToTimeFormat(time), false);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    } else if (previousScoresNames.size() == 1) {
      // need to sort it first
      if (time > convertTimeFormatToSeconds(previousScoresTimes.get(0))) {
        System.out.println("Hello world 1");
        System.out.println("Third: " + previousScoresNames.size());

        previousScoresTimes.add(1, previousScoresTimes.get(0));
        previousScoresNames.add(1, previousScoresNames.get(0));
        previousScoresTimes.set(0, convertSecondsToTimeFormat(time));
        previousScoresNames.set(0, playerName);
        appendToCsv(2);
        return;
      } else {
        System.out.println("Hello world 2");
        System.out.println("Fourth: " + previousScoresNames.size());

        previousScoresTimes.add(1, convertSecondsToTimeFormat(time));
        previousScoresNames.add(1, playerName);
        appendToCsv(2);
        return;
      }
    } else if (previousScoresNames.size() == 2) {
      // need to sort it first
      if (time > convertTimeFormatToSeconds(previousScoresTimes.get(0))) {
        System.out.println("Hello world 3");

        previousScoresTimes.add(2, previousScoresTimes.get(1));
        previousScoresNames.add(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, previousScoresTimes.get(0));
        previousScoresNames.set(1, previousScoresNames.get(0));
        previousScoresTimes.set(0, convertSecondsToTimeFormat(time));
        previousScoresNames.set(0, playerName);
        appendToCsv(3);
        return;
      } else if (time > convertTimeFormatToSeconds(previousScoresTimes.get(1))) {
        System.out.println("Hello world 4");

        previousScoresTimes.add(2, previousScoresTimes.get(1));
        previousScoresNames.add(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, convertSecondsToTimeFormat(time));
        previousScoresNames.set(1, playerName);
        appendToCsv(3);
        return;
      } else {
        System.out.println("Hello world 5");

        previousScoresTimes.add(2, convertSecondsToTimeFormat(time));
        previousScoresNames.add(2, playerName);
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
        previousScoresNames.set(0, playerName);
        appendToCsv(3);
        return;
      } else if ((time > convertTimeFormatToSeconds(previousScoresTimes.get(i))) && i == 1) {
        previousScoresTimes.set(2, previousScoresTimes.get(1));
        previousScoresNames.set(2, previousScoresNames.get(1));
        previousScoresTimes.set(1, convertSecondsToTimeFormat(time));
        previousScoresNames.set(1, playerName);
        appendToCsv(3);
        return;
      } else if ((time > convertTimeFormatToSeconds(previousScoresTimes.get(i))) && i == 2) {
        previousScoresTimes.set(2, convertSecondsToTimeFormat(time));
        previousScoresNames.set(2, playerName);
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

  public static void setPlayerName(String name) {
    playerName = name;
  }

  public static String getPlayerName() {
    return playerName;
  }

  public static ArrayList<String> getScoresForStartPage() {
    // Need to read the csv and put the values into an array
    ArrayList<String> scoreBoardValues = new ArrayList<>();
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
        scoreBoardValues.add(split[0]);
        scoreBoardValues.add(split[1]);
        line++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1); // exit the program
    }
    return scoreBoardValues;
  }

  /**
   * Converts a time duration from milliseconds to a formatted string in the format "MM:SS".
   *
   * @param milliseconds the time duration in milliseconds
   * @return a formatted string representing the time duration in "MM:SS" format
   */
  public static String formatTime(double milliseconds) {
    // Convert milliseconds to minutes and seconds
    int minutes = (int) milliseconds / 60000;
    int seconds = (int) (milliseconds / 1000) - (minutes * 60);
    // Appends a 0 to maintain the format of XX:XX even when the time is less than 10 units.
    String minuteAppend = (minutes < 10) ? "0" : "";
    String secondAppend = (seconds < 10) ? "0" : "";
    return (minuteAppend + minutes + ":" + secondAppend + seconds);
  }

  /**
   * Adds the specified time to the total time used. This method will be called at the end of the
   * investigation scenes and guess scene.
   *
   * @param time the time to be added to the total time used, in milliseconds
   */
  public static void setTimeUsed(double time) {
    timeUsed = timeUsed + time;
    System.out.println(timeUsed);
  }

  public static double getTimeUsed(
      GameStateContext context, boolean isAllSuspectsSpokenTo, boolean isAnyClueFound) {
    return (int) (timeUsed / 1000);
  }

  public static void checkConditions(
      GameStateContext context,
      boolean isAllSuspectsSpokenTo,
      boolean isAnyClueFound,
      Timeline timeline) {
    // Program switch to guess scene here ONLY if clues and suspects have been
    // correctly interacted with
    // Before switching state, make sure the game is still in the game started state
    // and that we havent already switched state. Otherwise it will cause a bug
    if (!(context.getGameState().equals(context.getGameStartedState()))) {
      System.out.println("hello g " + context.getGameState());
      timeline.stop();
      return;
    }
    if (context.isAllSuspectsSpokenTo()
        && CrimeSceneController.isAnyClueFound()
        && context.getGameState().equals(context.getGameStartedState())) {
      context.setState(context.getGuessingState());
      try {
        timeline.stop();
        setTimeUsed(0);
        App.setRoot("guess");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
      // Stop the timer here, as once the suer switch to guessing state, they aren't
      // coming back
      timeline.stop();
    } else if (!context.isAllSuspectsSpokenTo()
        && CrimeSceneController.isAnyClueFound()
        && context.getGameState().equals(context.getGameStartedState())) {
      context.setState(context.getGameOverState());
      // prints the message when the user did not speak to all suspects
      GameOverController.setOutputText(
          "You did not speak to every suspect during your investigation!\n"
              + "Without doing this, the investigation is incomplete!\n"
              + "Click play again to replay.");
      try {
        timeline.stop();
        App.setRoot("gamelost");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (context.isAllSuspectsSpokenTo()
        && !CrimeSceneController.isAnyClueFound()
        && context.getGameState().equals(context.getGameStartedState())) {
      context.setState(context.getGameOverState());
      // prints the message when the user did not find any clues
      GameOverController.setOutputText(
          "You did not find any clues in the crime scene!\n"
              + "Finding clues is vital to conduting a good investigation!\n"
              + "Click play again to replay");
      try {
        timeline.stop();
        App.setRoot("gamelost");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (!context.isAllSuspectsSpokenTo()
        && !CrimeSceneController.isAnyClueFound()
        && context.getGameState().equals(context.getGameStartedState())) {
      context.setState(context.getGameOverState());
      // prints the message when the user did not find any clues and did not speak
      // to every suspects
      GameOverController.setOutputText(
          "You did not inspect the crime scene for clues or speak to every"
              + " suspect!\n"
              + "These steps are vital in any investigation.\n"
              + "Click play again to replay.");
      try {
        timeline.stop();
        App.setRoot("gamelost");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
