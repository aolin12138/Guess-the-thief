package nz.ac.auckland.se206;

public class PreviousScore {
  private String timeCompleted;
  private int roundNumber;

  public PreviousScore(String timeCompleted, int roundNumber) {
    this.timeCompleted = timeCompleted;
    this.roundNumber = 600 - roundNumber; // Calculate time used from time remaining
  }
}
