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
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;

public class LocationTypeRadius extends LocationType {

    public final MeasureUnit measureUnit;
    public final float radius;

    public static LocationTypeRadius fromCurrentLocationSelection(LocationSelection selection) {
        if (selection instanceof RadiusLocationSelection) {
            FloatWithUnit floatWithUnit = ((RadiusLocationSelection) selection).getRadius();
            return new LocationTypeRadius(
                    R.string.radius, true, floatWithUnit.getValue(), floatWithUnit.getUnit()
            );
        } else {
            return new LocationTypeRadius(R.string.radius, false);
        }
    }

    private LocationTypeRadius(@StringRes int displayNameRes, boolean isEnabled) {
        this(displayNameRes, isEnabled, 0f, MeasureUnit.DIP);
    }

    private LocationTypeRadius(
            @StringRes int displayNameRes,
            boolean isEnabled,
            float radius,
            MeasureUnit measureUnit
    ) {
        super(displayNameRes, isEnabled);
        this.radius = radius;
        this.measureUnit = measureUnit;
    }

    @Override
    public LocationSelection buildLocationSelection() {
        return new RadiusLocationSelection(new FloatWithUnit(radius, measureUnit));
    }
}
