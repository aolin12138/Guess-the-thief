package nz.ac.auckland.se206;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ClueManager {
  private ImageView imageView;

  private ScaleTransition scaleTransitionIn;
  private ColorAdjust colorAdjustIn;
  private DropShadow dropShadowIn;
  private Timeline brightnessTransitionIn;
  private Timeline shadowTransitionIn;

  private ScaleTransition scaleTransitionOut;
  private ColorAdjust colorAdjustOut;
  private DropShadow dropShadowOut;
  private Timeline brightnessTransitionOut;
  private Timeline shadowTransitionOut;

  private Timeline brightnessTransitionStay;
  private ScaleTransition clickScale;

  @SuppressWarnings("unused")
  private int originalX = 0;

  private boolean isClicked = false;

  /**
   * Constructor for the ClueManager class which manages the effects of the image.
   *
   * @param imageView class which manages the effects of the image.
   */
  public ClueManager(ImageView imageView) {
    this.imageView = imageView;

    if (imageView != null) {
      originalX = (int) imageView.getLayoutX();
    } else {
      originalX = 0;
    }

    // new instance of a scale transition
    scaleTransitionIn = new ScaleTransition(Duration.millis(100), imageView);
    scaleTransitionIn.setFromX(1.0);
    scaleTransitionIn.setFromY(1.0);
    scaleTransitionIn.setToX(1.2);
    scaleTransitionIn.setToY(1.2);
    scaleTransitionIn.setCycleCount(1);

    // new instance of a color adjust effect
    colorAdjustIn = new ColorAdjust();
    colorAdjustIn.setBrightness(0);

    // new instance of a drop shadow effect
    dropShadowIn = new DropShadow();
    dropShadowIn.setRadius(0);
    dropShadowIn.setOffsetX(0);
    dropShadowIn.setOffsetY(0);
    dropShadowIn.setColor(javafx.scene.paint.Color.GRAY);

    dropShadowIn.setInput(colorAdjustIn);

    // brigtness transition in new timeline
    brightnessTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjustIn.brightnessProperty(), 0)),
            new KeyFrame(
                Duration.millis(100), new KeyValue(colorAdjustIn.brightnessProperty(), 0.45)));
    brightnessTransitionIn.setCycleCount(1);
    // shadow transition in new timeline
    shadowTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadowIn.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(dropShadowIn.radiusProperty(), 10)));
    shadowTransitionIn.setCycleCount(1);
    // new instance of a scale transition out
    scaleTransitionOut = new ScaleTransition(Duration.millis(100), imageView);
    scaleTransitionOut.setFromX(1.2);
    scaleTransitionOut.setFromY(1.2);
    scaleTransitionOut.setToX(1.0);
    scaleTransitionOut.setToY(1.0);
    scaleTransitionOut.setCycleCount(1);

    colorAdjustOut = new ColorAdjust();
    colorAdjustOut.setBrightness(0);
    // new instance of a drop shadow effect out
    dropShadowOut = new DropShadow();
    dropShadowOut.setRadius(10);
    dropShadowOut.setOffsetX(0);
    dropShadowOut.setOffsetY(0);
    dropShadowOut.setColor(javafx.scene.paint.Color.GRAY);

    dropShadowOut.setInput(colorAdjustOut);
    // brightness transition out new timeline
    brightnessTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjustOut.brightnessProperty(), 0.45)),
            new KeyFrame(
                Duration.millis(100), new KeyValue(colorAdjustOut.brightnessProperty(), 0)));
    brightnessTransitionOut.setCycleCount(1);
    // shadow transition out new timeline
    shadowTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadowOut.radiusProperty(), 10)),
            new KeyFrame(Duration.millis(100), new KeyValue(dropShadowOut.radiusProperty(), 0)));
    shadowTransitionOut.setCycleCount(1);
    // brightness transition stay new timeline
    brightnessTransitionStay =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.1),
                new KeyValue(dropShadowIn.radiusProperty(), 10),
                new KeyValue(colorAdjustIn.brightnessProperty(), 0)));
    brightnessTransitionIn.setCycleCount(1);

    clickScale = new ScaleTransition(Duration.seconds(0.1), imageView);
    clickScale.setToX(1.2);
    clickScale.setToY(1.2);
  }

  /**
   * This method is called when the mouse hovers in the image. It will animate the image to hover in
   */
  public void hoverIn() {
    // check if the image is clicked
    if (isClicked) {
      return;
    }
    // sets the effect of the image view to the drop shadow effect
    imageView.setEffect(dropShadowIn);
    // stops the scale transition out, brightness transition out and shadow transition out
    scaleTransitionOut.stop();
    brightnessTransitionOut.stop();
    shadowTransitionOut.stop();
    brightnessTransitionStay.stop();
    // plays the scale transition in, brightness transition in and shadow transition in
    scaleTransitionIn.play();
    brightnessTransitionIn.play();
    shadowTransitionIn.play();
  }

  /**
   * This method is called when the mouse hovers out of the image. It will animate the image to
   * hover
   */
  public void hoverOut() {
    // check if the image is clicked
    if (isClicked) {
      return;
    }
    // sets the effect of the image view to the drop shadow effect
    imageView.setEffect(dropShadowOut);

    // stops the scale transition in, brightness transition in, shadow transition in and brightness
    scaleTransitionIn.stop();
    brightnessTransitionIn.stop();
    shadowTransitionIn.stop();
    brightnessTransitionStay.stop();
    // plays the scale transition out, brightness transition out and shadow transition out
    scaleTransitionOut.play();
    brightnessTransitionOut.play();
    shadowTransitionOut.play();
  }

  /** This method is called when it needs to stay at current effect. */
  public void stayCurrentEffect() {
    imageView.setEffect(dropShadowOut);
  }

  /**
   * Set the image view to vew the image.
   *
   * @param imageView Adding more words due to requirements for description.
   */
  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  /**
   * This method is called when the image is clicked. It will animate the image to be clicked.
   *
   * @return Adding more words due to requirements for description.
   */
  public ImageView getImageView() {
    return imageView;
  }

  /** This method is called when the image is clicked. It will animate the image to be clicked */
  public void clicked() {
    // new instance of a drop shadow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(10);
    dropShadow.setOffsetX(0);
    dropShadow.setOffsetY(0);
    dropShadow.setColor(javafx.scene.paint.Color.WHITE);

    // new instance of a color adjust effect
    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(0);
    // sets the effect of the image view to the drop shadow effect
    dropShadow.setInput(colorAdjust);
    // sets the scale of the image view to 1.1
    imageView.setEffect(dropShadow);
    imageView.setScaleX(1.1);
    imageView.setScaleY(1.1);
    isClicked = true;
  }

  /**
   * This method is called when the image is unclicked. It will animate the image to be unclicked
   */
  public void unclicked() {
    isClicked = false;
    hoverOut();
  }
}
