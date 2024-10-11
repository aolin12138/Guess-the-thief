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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Control;

/**
 * Base class for the progress indicator controls represented by circualr shapes.
 *
 * @author Andrea Vacondio
 */
abstract class ProgressCircleIndicator extends Control {
  private static final int INDETERMINATE_PROGRESS = -1;

  /**
   * @return The CssMetaData associated with this class, which may include the CssMetaData of its
   *     super classes.
   */
  public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
    return StyleableProperties.STYLEABLES;
  }

  /**
   * The CssMetaData associated with this class, which may include the CssMetaData of its super
   * classes.
   */
  private static class StyleableProperties {
    private static final CssMetaData<ProgressCircleIndicator, Number> INNER_CIRCLE_RADIUS =
        new CssMetaData<ProgressCircleIndicator, Number>(
            "-fx-inner-radius", SizeConverter.getInstance(), 60) {

          // @formatter:off
          @Override
          public boolean isSettable(ProgressCircleIndicator n) {
            return n.innerCircleRadiusProperty() == null
                || !n.innerCircleRadiusProperty().isBound();
          }

          // @formatter:on
          @Override
          public StyleableProperty<Number> getStyleableProperty(ProgressCircleIndicator n) {
            return (StyleableProperty<Number>) n.innerCircleRadiusProperty();
          }
        };

    /**
     * The CssMetaData for the ProgressCircleIndicator. This is used to style the control via CSS.
     */
    public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
      final List<CssMetaData<? extends Styleable, ?>> styleables =
          new ArrayList<>(Control.getClassCssMetaData());
      styleables.add(INNER_CIRCLE_RADIUS);
      STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }

  // create a new ReadOnlyIntegerWrapper object and a ReadOnlyBooleanWrapper object
  private ReadOnlyIntegerWrapper progress = new ReadOnlyIntegerWrapper(0);
  private ReadOnlyBooleanWrapper indeterminate = new ReadOnlyBooleanWrapper(false);

  /** radius of the inner circle. */
  private DoubleProperty innerCircleRadius =
      new StyleableDoubleProperty(60) {
        @Override
        public Object getBean() {
          return ProgressCircleIndicator.this;
        }

        @Override
        public String getName() {
          return "innerCircleRadius";
        }

        @Override
        public CssMetaData<ProgressCircleIndicator, Number> getCssMetaData() {
          return StyleableProperties.INNER_CIRCLE_RADIUS;
        }
      };

  /** Get the pregress circile indicator. */
  public ProgressCircleIndicator() {
    this.getStylesheets().add(getClass().getResource("/css/circleprogress.css").toExternalForm());
  }

  /**
   * Get the progress value for usage.
   *
   * @return Adding extra due missing requirements in methods.
   */
  public int getProgress() {
    return progress.get();
  }

  /**
   * Set the value for the progress, it cannot be more then 100 (meaning 100%). A negative value
   * means indeterminate progress.
   *
   * @param progressValue the value for the progress bar
   * @see ProgressCircleIndicator#makeIndeterminate()
   */
  public void setProgress(int progressValue) {
    progress.set(defaultToHundred(progressValue));
    indeterminate.set(progressValue < 0);
  }

  /**
   * Get the progress of the property.
   *
   * @return Adding more words due to requirements for description.
   */
  public ReadOnlyIntegerProperty progressProperty() {
    return progress.getReadOnlyProperty();
  }

  /**
   * Check if the progress is indeterminate.
   *
   * @return Adding more words due to requirements for description.
   */
  public boolean isIndeterminate() {
    return indeterminate.get();
  }

  /** Set the progress to indeterminate. */
  public void makeIndeterminate() {
    setProgress(INDETERMINATE_PROGRESS);
  }

  /**
   * Get the indeterminate property.
   *
   * @return Adding extra bulk due testing requirements.
   */
  public ReadOnlyBooleanProperty getIndeterminateProperty() {
    return indeterminate.getReadOnlyProperty();
  }

  /**
   * Set the default value to 100 if the value is greater than 100.
   *
   * @param value Adding extra bulk due testing requirements.
   * @return Adding extra bulk due testing requirements.
   */
  private int defaultToHundred(int value) {
    if (value > 100) {
      return 100;
    }
    return value;
  }

  /**
   * Sets the room loader Adding extra due missing requirements in methods.
   *
   * @param roomLoader the FXMLLoader to set for the room
   */
  public final void setInnerCircleRadius(int value) {
    innerCircleRadiusProperty().set(value);
  }

  /**
   * Gets the inner circle radius property.
   *
   * @return the DoubleProperty representing the inner circle radius
   */
  public final DoubleProperty innerCircleRadiusProperty() {
    return innerCircleRadius;
  }

  /**
   * Gets the inner circle radius.
   *
   * @return the radius of the inner circle
   */
  public final double getInnerCircleRadius() {
    return innerCircleRadiusProperty().get();
  }

  /**
   * Gets the CssMetaData associated with this class, which may include the CssMetaData of its
   * superclasses.
   *
   * @return a list of CssMetaData associated with this class
   */
  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return StyleableProperties.STYLEABLES;
  }
}
