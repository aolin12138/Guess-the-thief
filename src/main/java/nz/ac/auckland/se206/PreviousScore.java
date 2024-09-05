package nz.ac.auckland.se206;

public class PreviousScore {
  private String roundNumber;
  private String timeUsed;

  public PreviousScore(String roundNumber, int secondsRemaining) {
    this.roundNumber = roundNumber;
    this.timeUsed = Utils.convertSecondsToTimeFormat(secondsRemaining);
  }

  public String getRoundNumber() {
    return this.roundNumber;
  }

  public String getTimeUsed() {
    return this.timeUsed;
  }
}
