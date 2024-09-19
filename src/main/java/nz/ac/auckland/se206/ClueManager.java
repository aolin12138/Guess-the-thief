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
  public ImageView imageView;

  public ScaleTransition scaleTransitionIn;
  public ColorAdjust colorAdjustIn;
  public DropShadow dropShadowIn;
  public Timeline brightnessTransitionIn;
  public Timeline shadowTransitionIn;

  public ScaleTransition scaleTransitionOut;
  public ColorAdjust colorAdjustOut;
  public DropShadow dropShadowOut;
  public Timeline brightnessTransitionOut;
  public Timeline shadowTransitionOut;

  public Timeline brightnessTransitionStay;
  public Timeline shadowTransitionStay;
  public ScaleTransition clickScale;

  public int originalX;

  private boolean isClicked = false;

  public ClueManager(ImageView imageView) {
    this.imageView = imageView;

    if (imageView != null) {
      originalX = (int) imageView.getLayoutX();
    } else {
      originalX = 0;
    }

    scaleTransitionIn = new ScaleTransition(Duration.millis(100), imageView);
    scaleTransitionIn.setFromX(1.0);
    scaleTransitionIn.setFromY(1.0);
    scaleTransitionIn.setToX(1.2);
    scaleTransitionIn.setToY(1.2);
    scaleTransitionIn.setCycleCount(1);

    colorAdjustIn = new ColorAdjust();
    colorAdjustIn.setBrightness(0);

    dropShadowIn = new DropShadow();
    dropShadowIn.setRadius(0);
    dropShadowIn.setOffsetX(0);
    dropShadowIn.setOffsetY(0);
    dropShadowIn.setColor(javafx.scene.paint.Color.GRAY);

    dropShadowIn.setInput(colorAdjustIn);

    brightnessTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjustIn.brightnessProperty(), 0)),
            new KeyFrame(
                Duration.millis(100), new KeyValue(colorAdjustIn.brightnessProperty(), 0.45)));
    brightnessTransitionIn.setCycleCount(1);

    shadowTransitionIn =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadowIn.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(dropShadowIn.radiusProperty(), 10)));
    shadowTransitionIn.setCycleCount(1);

    scaleTransitionOut = new ScaleTransition(Duration.millis(100), imageView);
    scaleTransitionOut.setFromX(1.2);
    scaleTransitionOut.setFromY(1.2);
    scaleTransitionOut.setToX(1.0);
    scaleTransitionOut.setToY(1.0);
    scaleTransitionOut.setCycleCount(1);

    colorAdjustOut = new ColorAdjust();
    colorAdjustOut.setBrightness(0);

    dropShadowOut = new DropShadow();
    dropShadowOut.setRadius(10);
    dropShadowOut.setOffsetX(0);
    dropShadowOut.setOffsetY(0);
    dropShadowOut.setColor(javafx.scene.paint.Color.GRAY);

    dropShadowOut.setInput(colorAdjustOut);

    brightnessTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjustOut.brightnessProperty(), 0.45)),
            new KeyFrame(
                Duration.millis(100), new KeyValue(colorAdjustOut.brightnessProperty(), 0)));
    brightnessTransitionOut.setCycleCount(1);

    shadowTransitionOut =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dropShadowOut.radiusProperty(), 10)),
            new KeyFrame(Duration.millis(100), new KeyValue(dropShadowOut.radiusProperty(), 0)));
    shadowTransitionOut.setCycleCount(1);

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

  public void hoverIn() {
    if (isClicked) {
      return;
    }
    imageView.setEffect(dropShadowIn);

    scaleTransitionOut.stop();
    brightnessTransitionOut.stop();
    shadowTransitionOut.stop();
    brightnessTransitionStay.stop();

    scaleTransitionIn.play();
    brightnessTransitionIn.play();
    shadowTransitionIn.play();
  }

  public void hoverOut() {
    if (isClicked) {
      return;
    }
    imageView.setEffect(dropShadowOut);

    scaleTransitionIn.stop();
    brightnessTransitionIn.stop();
    shadowTransitionIn.stop();
    brightnessTransitionStay.stop();

    scaleTransitionOut.play();
    brightnessTransitionOut.play();
    shadowTransitionOut.play();
  }

  public void stayCurrentEffect() {
    imageView.setEffect(dropShadowOut);
  }

  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  public ImageView getImageView() {
    return imageView;
  }

  public void clicked() {
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(10);
    dropShadow.setOffsetX(0);
    dropShadow.setOffsetY(0);
    dropShadow.setColor(javafx.scene.paint.Color.WHITE);

    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(0);

    dropShadow.setInput(colorAdjust);

    imageView.setEffect(dropShadow);
    imageView.setScaleX(1.1);
    imageView.setScaleY(1.1);
    isClicked = true;
  }

  public void unclicked() {
    isClicked = false;
    hoverOut();
  }
}
