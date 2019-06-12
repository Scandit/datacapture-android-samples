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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes;

import androidx.annotation.StringRes;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RectangularLocationSelection;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.SizeWithUnit;

public class LocationTypeRectangular extends LocationType {

    public static LocationTypeRectangular fromCurrentLocationSelectionAndSettings(
            LocationSelection selection, SettingsManager settingsManager
    ) {
        return new LocationTypeRectangular(
                R.string.rectangular,
                selection instanceof RectangularLocationSelection,
                settingsManager.getLocationSelectionRectangularSizeSpecification(),
                settingsManager.getLocationSelectionRectangularWidth(),
                settingsManager.getLocationSelectionRectangularHeight(),
                settingsManager.getLocationSelectionRectangularHeightAspect(),
                settingsManager.getLocationSelectionRectangularWidthAspect()
        );
    }

    private SizeSpecification sizeSpecification;
    private FloatWithUnit width;
    private FloatWithUnit height;
    private float heightAspectRatio;
    private float widthAspectRatio;

    private LocationTypeRectangular(
            @StringRes int displayNameRes,
            boolean isEnabled,
            SizeSpecification sizeSpecification,
            FloatWithUnit width,
            FloatWithUnit height,
            float heightAspectRatio,
            float widthAspectRatio
    ) {
        super(displayNameRes, isEnabled);
        this.sizeSpecification = sizeSpecification;
        this.width = width;
        this.height = height;
        this.heightAspectRatio = heightAspectRatio;
        this.widthAspectRatio = widthAspectRatio;
    }

    @Override
    public LocationSelection buildLocationSelection() {
        switch (sizeSpecification) {
            case WIDTH_AND_HEIGHT:
                return RectangularLocationSelection.withSize(new SizeWithUnit(width, height));
            case WIDTH_AND_HEIGHT_ASPECT:
                return RectangularLocationSelection.withWidthAndAspectRatio(
                        width, heightAspectRatio
                );
            case HEIGHT_AND_WIDTH_ASPECT:
                return RectangularLocationSelection.withHeightAndAspectRatio(
                        height, widthAspectRatio
                );
        }
        return null;
    }

    public SizeSpecification getSizeSpecification() {
        return sizeSpecification;
    }

    public void setSizeSpecification(SizeSpecification sizeSpecification) {
        this.sizeSpecification = sizeSpecification;
    }

    public float getHeightAspectRatio() {
        return heightAspectRatio;
    }

    public void setHeightAspectRatio(float heightAspectRatio) {
        this.heightAspectRatio = heightAspectRatio;
    }

    public float getWidthAspectRatio() {
        return widthAspectRatio;
    }

    public void setWidthAspectRatio(float widthAspectRatio) {
        this.widthAspectRatio = widthAspectRatio;
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
}
