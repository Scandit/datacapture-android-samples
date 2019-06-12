/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

public class ViewfinderTypeRectangular extends ViewfinderType {

    public static ViewfinderTypeRectangular fromCurrentViewfinderAndSettings(
            Viewfinder currentViewFinder, SettingsManager settingsManager
    ) {
        return new ViewfinderTypeRectangular(
                currentViewFinder instanceof RectangularViewfinder,
                settingsManager.getRectangularViewfinderColor(),
                settingsManager.getRectangularViewfinderSizeSpecification(),
                settingsManager.getRectangularViewfinderWidth(),
                settingsManager.getRectangularViewfinderHeight(),
                settingsManager.getRectangularViewfinderWidthAspect(),
                settingsManager.getRectangularViewfinderHeightAspect()
        );
    }

    private Color color;
    private SizeSpecification sizeSpecification;

    private FloatWithUnit width;
    private FloatWithUnit height;
    private float widthAspect;
    private float heightAspect;

    private ViewfinderTypeRectangular(
            boolean enabled,
            Color color,
            SizeSpecification sizeSpecification,
            FloatWithUnit width,
            FloatWithUnit height,
            float widthAspect,
            float heightAspect
    ) {
        super(R.string.rectangular, enabled);
        this.color = color;
        this.sizeSpecification = sizeSpecification;
        this.width = width;
        this.height = height;
        this.widthAspect = widthAspect;
        this.heightAspect = heightAspect;
    }

    public SizeSpecification getSizeSpecification() {
        return sizeSpecification;
    }

    public void setSizeSpecification(SizeSpecification sizeSpecification) {
        this.sizeSpecification = sizeSpecification;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public FloatWithUnit getWidth() {
        return width;
    }

    public void setWidth(FloatWithUnit width) {
        this.width = width;
    }

    public FloatWithUnit getHeight() {
        return height;
    }

    public void setHeight(FloatWithUnit height) {
        this.height = height;
    }

    public float getHeightAspect() {
        return heightAspect;
    }

    public void setHeightAspect(float heightAspect) {
        this.heightAspect = heightAspect;
    }

    public float getWidthAspect() {
        return widthAspect;
    }

    public void setWidthAspect(float widthAspect) {
        this.widthAspect = widthAspect;
    }

    @Nullable
    @Override
    public Viewfinder buildViewfinder() {
        RectangularViewfinder viewfinder = new RectangularViewfinder();
        viewfinder.setColor(color.color);
        switch (sizeSpecification) {
            case WIDTH_AND_HEIGHT:
                viewfinder.setSize(new SizeWithUnit(width, height));
                break;
            case WIDTH_AND_HEIGHT_ASPECT:
                viewfinder.setWidthAndAspectRatio(width, heightAspect);
                break;
            case HEIGHT_AND_WIDTH_ASPECT:
                viewfinder.setHeightAndAspectRatio(height, widthAspect);
                break;
        }
        return viewfinder;
    }

    public enum Color {
        DEFAULT(new RectangularViewfinder().getColor(), R.string._default),
        BLUE(android.graphics.Color.BLUE, R.string.blue),
        BLACK(android.graphics.Color.BLACK, R.string.black);

        @ColorInt public final int color;
        @StringRes public final int displayName;

        Color(@ColorInt int color, @StringRes int displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }
}
