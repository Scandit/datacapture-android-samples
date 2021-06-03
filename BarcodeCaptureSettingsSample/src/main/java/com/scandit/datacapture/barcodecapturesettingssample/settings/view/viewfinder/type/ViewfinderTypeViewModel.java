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

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.ui.viewfinder.AimerViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

@SuppressWarnings("WeakerAccess")
public class ViewfinderTypeViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    public ViewfinderType getCurrentViewfinderType() {
        Viewfinder viewfinder = getCurrentViewfinder();
        if (viewfinder instanceof RectangularViewfinder) {
            return ViewfinderTypeRectangular.fromCurrentViewfinderAndSettings(
                    viewfinder, settingsManager
            );
        } else if (viewfinder instanceof LaserlineViewfinder) {
            return ViewfinderTypeLaserline.fromCurrentViewfinderAndSettings(
                    viewfinder, settingsManager
            );
        } else if (viewfinder instanceof AimerViewfinder) {
            return ViewfinderTypeAimer.fromCurrentViewfinderAndSettings(
                    viewfinder, settingsManager
            );
        } else {
            return ViewfinderTypeNone.fromCurrentViewFinder(viewfinder);
        }
    }

    public Viewfinder getCurrentViewfinder() {
        return settingsManager.getCurrentViewfinder();
    }

    public void updateViewfinder() {
        updateViewfinder(getCurrentViewfinderType());
    }

    public void updateViewfinder(ViewfinderType viewfinderType) {
        settingsManager.setViewfinder(viewfinderType.buildViewfinder());
    }

    public LaserlineViewfinderStyle getLaserlineViewfinderStyle() {
        return settingsManager.getLaserlineViewfinderStyle();
    }

    public RectangularViewfinderStyle getRectangularViewfinderStyle() {
        return settingsManager.getRectangularViewfinderStyle();
    }
}
