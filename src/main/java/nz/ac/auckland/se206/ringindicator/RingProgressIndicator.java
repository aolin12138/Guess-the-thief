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
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Progress indicator showing a filling arc.
 *
 * @author Andrea Vacondio
 */
public class RingProgressIndicator extends ProgressCircleIndicator {

  private static class StyleableProperties {
    private static final CssMetaData<RingProgressIndicator, Number> RING_WIDTH =
        new CssMetaData<RingProgressIndicator, Number>(
            "-fx-ring-width", SizeConverter.getInstance(), 22) {

          // returns whether if it is settable or not
          @Override
          public boolean isSettable(RingProgressIndicator n) {
            return n.ringWidth == null || !n.ringWidth.isBound();
          }

          // gets the styleable property
          @Override
          public StyleableProperty<Number> getStyleableProperty(RingProgressIndicator n) {
            return (StyleableProperty<Number>) n.ringWidth;
          }
        };

    public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
      final List<CssMetaData<? extends Styleable, ?>> styleables =
          new ArrayList<>(Control.getClassCssMetaData());
      styleables.addAll(ProgressCircleIndicator.getClassCssMetaData());
      styleables.add(RING_WIDTH);
      STYLEABLES = Collections.unmodifiableList(styleables);
    }
  }

  /** thickness of the ring indicator. */
  private DoubleProperty ringWidth =
      new StyleableDoubleProperty(22) {
        /**
         * Adding more words due to requirements for description.Adding more words due to
         * requirements for description.
         *
         * @return The Object that contains the property we are bound to.
         */
        @Override
        public Object getBean() {
          return RingProgressIndicator.this;
        }

        /**
         * Gets the name of the property this DoubleProperty is bound to.
         *
         * @return the name of the property, which is "ringWidth"
         */
        @Override
        public String getName() {
          return "ringWidth";
        }

        /**
         * @return The CssMetaData associated with this class, which may include the CssMetaData of
         *     its
         */
        @Override
        public CssMetaData<RingProgressIndicator, Number> getCssMetaData() {
          return StyleableProperties.RING_WIDTH;
        }
      };

  /** Creates a new instance of the RingProgressIndicator. */
  public RingProgressIndicator() {
    this.getStylesheets().add(getClass().getResource("/css/ringprogress.css").toExternalForm());
    this.getStyleClass().add("ringindicator");
  }

  /** creates a default skin for the RingProgressIndicator. */
  @Override
  protected Skin<?> createDefaultSkin() {
    return new RingProgressIndicatorSkin(this);
  }

  /**
   * sets te ring width of the RingProgressIndicator.
   *
   * @param value Adding more words due to requirements for description.
   */
  public final void setRingWidth(int value) {
    ringWidthProperty().set(value);
  }

  /**
   * gets the ring width of the RingProgressIndicator.
   *
   * @return Adding more words due to requirements for description.
   */
  public final DoubleProperty ringWidthProperty() {
    return ringWidth;
  }

  /**
   * gets the ring width of the RingProgressIndicator.
   *
   * @return Adding more words due to requirements for description.
   */
  public final double getRingWidth() {
    return ringWidthProperty().get();
  }

  /**
   * Adding more words due to requirements for description.Adding more words due to requirements for
   * description.
   *
   * @return The CssMetaData associated with this class, which may include the CssMetaData of its.
   */
  @Override
  public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
    return StyleableProperties.STYLEABLES;
  }
}
