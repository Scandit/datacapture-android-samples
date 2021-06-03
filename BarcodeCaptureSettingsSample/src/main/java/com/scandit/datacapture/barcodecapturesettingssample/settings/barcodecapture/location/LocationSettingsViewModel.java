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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationType;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeNone;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRadius;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRectangular;
import com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification;
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.area.RectangularLocationSelection;

import static com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification.HEIGHT_AND_WIDTH_ASPECT;
import static com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification.WIDTH_AND_HEIGHT;
import static com.scandit.datacapture.barcodecapturesettingssample.utils.SizeSpecification.WIDTH_AND_HEIGHT_ASPECT;

@SuppressWarnings("WeakerAccess")
public class LocationSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    private final SizeSpecification[] allowedSizeSpecifications = new SizeSpecification[] {
            WIDTH_AND_HEIGHT, WIDTH_AND_HEIGHT_ASPECT, HEIGHT_AND_WIDTH_ASPECT
    };

    public LocationType[] getAllowedLocationTypesAndEnabledState() {
        LocationSelection currentLocationSelection = getCurrentLocationSelection();
        return new LocationType[]{
                LocationTypeNone.fromCurrentLocationSelection(currentLocationSelection),
                LocationTypeRadius.fromCurrentLocationSelection(currentLocationSelection),
                LocationTypeRectangular.fromCurrentLocationSelectionAndSettings(
                        currentLocationSelection, settingsManager
                )
        };
    }

    public SizeSpecification[] getAllowedSizeSpecifications() {
        return allowedSizeSpecifications;
    }

    private LocationSelection getCurrentLocationSelection() {
        return settingsManager.getLocationSelection();
    }

    public LocationType getCurrentLocationType() {
        LocationSelection currentLocationSelection = getCurrentLocationSelection();
        if (currentLocationSelection instanceof RadiusLocationSelection) {
            return LocationTypeRadius.fromCurrentLocationSelection(currentLocationSelection);
        } else if (currentLocationSelection instanceof RectangularLocationSelection) {
            return LocationTypeRectangular.fromCurrentLocationSelectionAndSettings(
                    currentLocationSelection, settingsManager
            );
        } else {
            return LocationTypeNone.fromCurrentLocationSelection(currentLocationSelection);
        }
    }

    public void setLocationType(LocationType locationType) {
        if (locationType instanceof LocationTypeRectangular) {
            LocationTypeRectangular casted = (LocationTypeRectangular) locationType;
            settingsManager.setLocationSelectionRectangularWidth(casted.getWidth());
            settingsManager.setLocationSelectionRectangularHeight(casted.getHeight());
            settingsManager.setLocationSelectionRectangularHeightAspect(
                    casted.getHeightAspectRatio()
            );
            settingsManager.setLocationSelectionRectangularWidthAspect(
                    casted.getWidthAspectRatio()
            );
            settingsManager.setLocationSelectionRectangularSizeSpecification(
                    casted.getSizeSpecification()
            );
        }
        settingsManager.setLocationSelection(locationType.buildLocationSelection());
    }

    void setRectangularLocationSizeSpec(SizeSpecification spec) {
        LocationType currentLocation = getCurrentLocationType();
        if (currentLocation instanceof LocationTypeRectangular) {
            ((LocationTypeRectangular) currentLocation).setSizeSpecification(spec);
            setLocationType(currentLocation);
        }
    }

    void setRectangularLocationHeightAspect(float heightAspect) {
        LocationType currentLocation = getCurrentLocationType();
        if (currentLocation instanceof LocationTypeRectangular) {
            ((LocationTypeRectangular) currentLocation).setHeightAspectRatio(heightAspect);
            setLocationType(currentLocation);
        }
    }

    void setRectangularLocationWidthAspect(float widthAspect) {
        LocationType currentLocation = getCurrentLocationType();
        if (currentLocation instanceof LocationTypeRectangular) {
            ((LocationTypeRectangular) currentLocation).setWidthAspectRatio(widthAspect);
            setLocationType(currentLocation);
        }
    }
}
