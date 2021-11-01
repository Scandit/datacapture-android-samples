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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.controls;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;

@SuppressWarnings("WeakerAccess")
public class ControlsSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    boolean isZoomButtonEnabled() {
        return settingsManager.isZoomButtonEnabled();
    }

    void setZoomButtonEnabled(boolean enabled) {
        settingsManager.setZoomButtonEnabled(enabled);
    }

    boolean isTorchButtonEnabled() {
        return settingsManager.isTorchButtonEnabled();
    }

    void setTorchButtonEnabled(boolean enabled) {
        settingsManager.setTorchButtonEnabled(enabled);
    }

    boolean isCameraButtonEnabled() {
        return settingsManager.isCameraButtonEnabled();
    }

    void setCameraButtonEnabled(boolean enabled) {
        settingsManager.setCameraButtonEnabled(enabled);
    }
}
