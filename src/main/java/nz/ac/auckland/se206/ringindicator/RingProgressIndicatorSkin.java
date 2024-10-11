/*
 * Copyright (c) 2014, Andrea Vacondio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.ac.auckland.se206.ringindicator;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Skin of the ring progress indicator where an arc grows and by the progress value up to 100% where
 * the arc becomes a ring.
 *
 * @author Andrea Vacondio
 */
public class RingProgressIndicatorSkin implements Skin<RingProgressIndicator> {

  private final RingProgressIndicator indicator;
  private final Label percentLabel = new Label();
  private final Circle innerCircle = new Circle();
  private final Circle outerCircle = new Circle();
  private final StackPane container = new StackPane();
  private final Arc fillerArc = new Arc();
  private final RotateTransition transition =
      new RotateTransition(Duration.millis(2000), fillerArc);

  /** Sets the guess controller to this current instance. */
  public RingProgressIndicatorSkin(final RingProgressIndicator indicator) {
    this.indicator = indicator;
    initContainer(indicator);
    initFillerArc();
    container
        .widthProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              fillerArc.setCenterX(newVal.intValue() / 2);
            });
    container
        .heightProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              fillerArc.setCenterY(newVal.intValue() / 2);
            });
    innerCircle.getStyleClass().add("ringindicator-inner-circle");
    outerCircle.getStyleClass().add("ringindicator-outer-circle-secondary");
    updateRadii();

    // Listeners
    this.indicator
        .getIndeterminateProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              initIndeterminate(newVal);
            });
    this.indicator
        .progressProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              if (newVal.intValue() >= 0) {
                setProgressLabel(newVal.intValue());
                fillerArc.setLength(newVal.intValue() * -3.6);
              }
            });
    this.indicator
        .ringWidthProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              updateRadii();
            });
    innerCircle
        .strokeWidthProperty()
        .addListener(
            (e) -> {
              updateRadii();
            });
    innerCircle
        .radiusProperty()
        .addListener(
            (e) -> {
              updateRadii();
            });
    initTransition();
    initIndeterminate(indicator.isIndeterminate());
    initLabel(indicator.getProgress());
    indicator
        .visibleProperty()
        .addListener(
            (o, oldVal, newVal) -> {
              if (newVal && this.indicator.isIndeterminate()) {
                transition.play();
              } else {
                transition.pause();
              }
            });
    container.getChildren().addAll(fillerArc, outerCircle, innerCircle, percentLabel);
  }

  /**
   * This method sets the progress label.
   *
   * @param value
   */
  private void setProgressLabel(int value) {
    if (value >= 0) {
      percentLabel.setText(String.format("", value));
    }
  }

  /** This method initializes the transition. */
  private void initTransition() {
    transition.setAutoReverse(false);
    transition.setCycleCount(Animation.INDEFINITE);
    transition.setDelay(Duration.ZERO);
    transition.setInterpolator(Interpolator.LINEAR);
    transition.setByAngle(360);
  }

  /** This method initializes the filler arc. */
  private void initFillerArc() {
    fillerArc.setManaged(false);
    fillerArc.getStyleClass().add("ringindicator-filler");
    fillerArc.setStartAngle(90);
    fillerArc.setLength(indicator.getProgress() * -3.6);
  }

  /**
   * This method initializes the container.
   *
   * @param indicator the ring progress indicator to set the container for the skin.
   */
  private void initContainer(final RingProgressIndicator indicator) {
    container.getStylesheets().addAll(indicator.getStylesheets());
    container.getStyleClass().addAll("circleindicator-container");
    container.setMaxHeight(Region.USE_PREF_SIZE);
    container.setMaxWidth(Region.USE_PREF_SIZE);
  }

  /** This method updates the radii of the circles and the arc. */
  private void updateRadii() {
    // The width of the ring
    double ringWidth = indicator.getRingWidth();
    double innerCircleHalfStrokeWidth = innerCircle.getStrokeWidth() / 2;
    double innerCircleRadius = indicator.getInnerCircleRadius();
    // The outer circle is the circle that contains the filler arc
    outerCircle.setRadius(innerCircleRadius + innerCircleHalfStrokeWidth + ringWidth);
    fillerArc.setRadiusY(innerCircleRadius + innerCircleHalfStrokeWidth - 1 + (ringWidth / 2));
    fillerArc.setRadiusX(innerCircleRadius + innerCircleHalfStrokeWidth - 1 + (ringWidth / 2));
    // The inner circle is the circle that contains the filler arc
    fillerArc.setStrokeWidth(ringWidth);
    innerCircle.setRadius(innerCircleRadius);
  }

  /**
   * Initializes the label for the ring progress indicator.
   *
   * <p>This method sets up the label properties such as font, color, and alignment.
   */
  private void initLabel(int value) {
    setProgressLabel(value);
    percentLabel.getStyleClass().add("circleindicator-label");
  }

  /**
   * This method initializes the indeterminate progress.
   *
   * @param newVal Adding extra bulk due testing requirements.
   */
  private void initIndeterminate(boolean newVal) {
    // This makes the percent label visible only when the progress is not indeterminate
    percentLabel.setVisible(!newVal);
    // If new val is true, the arc will be filled and the transition will play
    if (newVal) {
      fillerArc.setLength(360);
      fillerArc.getStyleClass().add("indeterminate");
      // if the indicator is visible, the transition will play
      if (indicator.isVisible()) {
        transition.play();
      }
      // if the indicator is not visible, the transition will stop
    } else {
      fillerArc.getStyleClass().remove("indeterminate");
      fillerArc.setRotate(0);
      transition.stop();
    }
  }

  /** This method returns the skinnable. */
  @Override
  public RingProgressIndicator getSkinnable() {
    return indicator;
  }

  /**
   * Returns the node associated with this skin.
   *
   * @return the node associated with this skin
   */
  @Override
  public Node getNode() {
    return container;
  }

  /**
   * Disposes of the resources used by this skin.
   *
   * <p>This method stops the transition associated with the ring progress indicator.
   */
  @Override
  public void dispose() {
    transition.stop();
  }
}
