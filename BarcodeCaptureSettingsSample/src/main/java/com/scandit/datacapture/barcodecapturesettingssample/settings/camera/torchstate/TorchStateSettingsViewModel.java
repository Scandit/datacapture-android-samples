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

package com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate;

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.type.TorchStateType;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.type.TorchStateTypeOff;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.torchstate.type.TorchStateTypeOn;
import com.scandit.datacapture.core.source.TorchState;

@SuppressWarnings("WeakerAccess")
public class TorchStateSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    public TorchStateType[] getAllTorchStateTypes() {
        TorchState currentTorchState = getCurrentTorchState();
        return new TorchStateType[]{
                TorchStateTypeOn.fromCurrentTorchState(currentTorchState),
                TorchStateTypeOff.fromCurrentTorchState(currentTorchState),
        };
    }

    private TorchState getCurrentTorchState() {
        return settingsManager.getTorchState();
    }

    public void setTorchStateType(TorchStateType torchStateType) {
        settingsManager.setTorchState(torchStateType.torchState);
    }
}
