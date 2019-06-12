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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.rectangular;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationType;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeNone;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRadius;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.location.locationtypes.LocationTypeRectangular;
import com.scandit.datacapture.core.area.LocationSelection;
import com.scandit.datacapture.core.area.RadiusLocationSelection;
import com.scandit.datacapture.core.area.RectangularLocationSelection;

public class LocationRectangularViewModel extends ViewModel {

    protected final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    private LocationType getCurrentLocationType() {
        LocationSelection locationSelection = getCurrentLocation();

        if (locationSelection instanceof RectangularLocationSelection) {
            return LocationTypeRectangular.fromCurrentLocationSelectionAndSettings(
                    locationSelection, settingsManager
            );
        } else if (locationSelection instanceof RadiusLocationSelection) {
            return LocationTypeRadius.fromCurrentLocationSelection(locationSelection);
        } else {
            return LocationTypeNone.fromCurrentLocationSelection(locationSelection);
        }
    }

    private LocationSelection getCurrentLocation() {
        return settingsManager.getLocationSelection();
    }

    void updateLocation() {
        updateLocation(getCurrentLocationType());
    }

    private void updateLocation(LocationType locationType) {
        settingsManager.setLocationSelection(locationType.buildLocationSelection());
    }
}
